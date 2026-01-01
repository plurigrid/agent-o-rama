(ns agent-o-rama.ephemeral
  "Ephemeral Key Management for Aptos Society.

   Provides Clojure bindings for AIP-61 keyless authentication
   with automatic ephemeral key generation and rotation.

   Integration with the TypeScript EphemeralKeyManager via MCP."
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io])
  (:import
   [java.security MessageDigest SecureRandom]
   [java.util Base64]))

;; ============================================================
;; Constants
;; ============================================================

(def ^:const DEFAULT-EXPIRY-SECS 86400)  ; 24 hours
(def ^:const MIN-REMAINING-VALIDITY-SECS 300)  ; 5 minutes
(def ^:const WORLDS (vec (map (comp keyword str char) (range (int \a) (inc (int \z))))))

;; ============================================================
;; Ephemeral Key State
;; ============================================================

(defonce ^:private ephemeral-keys (atom {}))

(defn- now-secs []
  (quot (System/currentTimeMillis) 1000))

(defn- generate-random-bytes [n]
  (let [bytes (byte-array n)
        random (SecureRandom.)]
    (.nextBytes random bytes)
    bytes))

(defn- bytes->hex [^bytes bs]
  (apply str (map #(format "%02x" (bit-and % 0xff)) bs)))

(defn- hex->bytes [^String s]
  (let [s (if (str/starts-with? s "0x") (subs s 2) s)]
    (byte-array
     (map #(unchecked-byte (Integer/parseInt % 16))
          (re-seq #".{2}" s)))))

(defn- sha256 [^String s]
  (let [md (MessageDigest/getInstance "SHA-256")]
    (.digest md (.getBytes s "UTF-8"))))

;; ============================================================
;; Key Generation
;; ============================================================

(defn generate-ephemeral-keypair
  "Generate a new ephemeral Ed25519 keypair.
   Returns a map with :private-key, :public-key, :created-at, :expires-at"
  ([]
   (generate-ephemeral-keypair DEFAULT-EXPIRY-SECS))
  ([expiry-secs]
   (let [now (now-secs)
         private-bytes (generate-random-bytes 32)]
     {:private-key (bytes->hex private-bytes)
      :public-key nil  ; Would need Ed25519 impl for real public key derivation
      :created-at now
      :expires-at (+ now expiry-secs)})))

(defn generate-from-seed
  "Generate ephemeral keypair from deterministic seed.
   Useful for consistent key derivation across sessions."
  ([seed]
   (generate-from-seed seed DEFAULT-EXPIRY-SECS))
  ([seed expiry-secs]
   (let [now (now-secs)
         private-bytes (sha256 seed)]
     {:private-key (bytes->hex private-bytes)
      :public-key nil
      :created-at now
      :expires-at (+ now expiry-secs)})))

;; ============================================================
;; Key Management
;; ============================================================

(defn get-or-create-key
  "Get existing key or create new one for world."
  [world-id]
  (let [key-id (keyword (str "world-" (name world-id)))
        existing (get @ephemeral-keys key-id)]
    (if (and existing
             (> (:expires-at existing)
                (+ (now-secs) MIN-REMAINING-VALIDITY-SECS)))
      existing
      (let [new-key (generate-ephemeral-keypair)]
        (swap! ephemeral-keys assoc key-id new-key)
        new-key))))

(defn key-valid?
  "Check if ephemeral key is still valid."
  [key-state]
  (and key-state
       (> (:expires-at key-state)
          (+ (now-secs) MIN-REMAINING-VALIDITY-SECS))))

(defn remaining-validity
  "Get remaining validity in seconds."
  [key-state]
  (if key-state
    (max 0 (- (:expires-at key-state) (now-secs)))
    0))

(defn rotate-key!
  "Force rotate key for a world."
  [world-id]
  (let [key-id (keyword (str "world-" (name world-id)))
        new-key (generate-ephemeral-keypair)]
    (swap! ephemeral-keys assoc key-id new-key)
    new-key))

(defn rotate-all!
  "Force rotate all keys."
  []
  (doseq [world WORLDS]
    (rotate-key! world))
  (keys @ephemeral-keys))

;; ============================================================
;; JWT Sourcing
;; ============================================================

(defn fetch-jwt
  "Fetch JWT from source.
   Source types:
   - {:type :env :value \"VAR_NAME\"}
   - {:type :file :value \"/path/to/jwt\"}
   - {:type :http :value \"https://...\"}
   - {:type :inline :value \"eyJ...\"}  ; For testing only"
  [{:keys [type value]}]
  (case type
    :env (or (System/getenv value)
             (throw (ex-info "JWT env var not found" {:var value})))
    :file (str/trim (slurp value))
    :http (throw (ex-info "HTTP JWT fetch not implemented" {:url value}))
    :inline value
    (throw (ex-info "Unknown JWT source type" {:type type}))))

(defn parse-jwt-source
  "Parse JWT source string.
   Format: 'type:value' e.g., 'env:MY_JWT_VAR'"
  [s]
  (let [idx (str/index-of s ":")]
    (if (nil? idx)
      {:type :env :value s}
      {:type (keyword (subs s 0 idx))
       :value (subs s (inc idx))})))

;; ============================================================
;; Keyless Config Building
;; ============================================================

(defn build-keyless-config
  "Build keyless config for MCP server.
   Returns map suitable for APTOS_KEYLESS_* env vars."
  [jwt world-id]
  (let [key-state (get-or-create-key world-id)]
    {:jwt jwt
     :ephemeral-private-key (:private-key key-state)
     :ephemeral-expiry-secs (:expires-at key-state)}))

(defn keyless-env-vars
  "Generate environment variables for keyless MCP config."
  [jwt world-id]
  (let [config (build-keyless-config jwt world-id)]
    {"APTOS_KEYLESS_JWT" (:jwt config)
     "APTOS_KEYLESS_EPK_PRIVATE_KEY" (:ephemeral-private-key config)
     "APTOS_KEYLESS_EPK_EXPIRY_SECS" (str (:ephemeral-expiry-secs config))
     "APTOS_WORLD_ID" (name world-id)}))

;; ============================================================
;; World Integration
;; ============================================================

(defn init-all-worlds!
  "Initialize ephemeral keys for all 26 worlds."
  []
  (doseq [world WORLDS]
    (get-or-create-key world))
  (count @ephemeral-keys))

(defn get-world-key-status
  "Get status of ephemeral key for a world."
  [world-id]
  (let [key-id (keyword (str "world-" (name world-id)))
        key-state (get @ephemeral-keys key-id)]
    {:world world-id
     :key-id key-id
     :exists? (some? key-state)
     :valid? (key-valid? key-state)
     :remaining-secs (remaining-validity key-state)
     :expires-at (:expires-at key-state)}))

(defn get-all-world-status
  "Get status of all 26 world ephemeral keys."
  []
  (into {}
        (for [world WORLDS]
          [world (get-world-key-status world)])))

;; ============================================================
;; MCP Integration
;; ============================================================

(defn mcp-keyless-args
  "Build MCP server args with keyless config.
   Returns vector suitable for ProcessBuilder."
  [jwt world-id & {:keys [network] :or {network "mainnet"}}]
  (let [env-vars (keyless-env-vars jwt world-id)]
    {:command "node"
     :args ["${APTOS_MCP_PATH}/dist/mcp/server.js"]
     :env (assoc env-vars "APTOS_NETWORK" network)}))

(defn start-mcp-with-keyless!
  "Start MCP server with keyless auth for a world.
   Returns the Process object."
  [jwt world-id & {:keys [network] :or {network "mainnet"}}]
  (let [{:keys [command args env]} (mcp-keyless-args jwt world-id :network network)
        mcp-path (or (System/getenv "APTOS_MCP_PATH")
                     (str (System/getenv "HOME") "/aptos-claude-agent"))
        full-args (mapv #(str/replace % "${APTOS_MCP_PATH}" mcp-path) args)
        pb (ProcessBuilder. (into [command] full-args))]
    ;; Set environment
    (doseq [[k v] env]
      (.put (.environment pb) k v))
    (.start pb)))

;; ============================================================
;; Cleanup
;; ============================================================

(defn clear-all-keys!
  "Clear all ephemeral keys (for cleanup)."
  []
  (reset! ephemeral-keys {})
  :cleared)

;; ============================================================
;; CLI Interface
;; ============================================================

(defn -main
  "CLI interface for ephemeral key management.

   Commands:
     generate [world]     Generate ephemeral key for world
     status [world]       Show key status
     status-all           Show all world key statuses
     rotate [world]       Force rotate key
     rotate-all           Force rotate all keys
     env-vars <jwt> <world>  Print env vars for keyless config"
  [& args]
  (let [[cmd & rest-args] args]
    (case cmd
      "generate" (let [world (keyword (or (first rest-args) "a"))]
                   (println "Generated key for" world)
                   (prn (get-or-create-key world)))

      "status" (let [world (keyword (or (first rest-args) "a"))]
                 (prn (get-world-key-status world)))

      "status-all" (doseq [[world status] (get-all-world-status)]
                     (println (format "%s: valid=%s remaining=%ds"
                                      (name world)
                                      (:valid? status)
                                      (:remaining-secs status))))

      "rotate" (let [world (keyword (or (first rest-args) "a"))]
                 (println "Rotated key for" world)
                 (prn (rotate-key! world)))

      "rotate-all" (do (rotate-all!)
                       (println "Rotated all" (count @ephemeral-keys) "keys"))

      "env-vars" (let [[jwt world] rest-args]
                   (when-not (and jwt world)
                     (println "Usage: env-vars <jwt> <world>")
                     (System/exit 1))
                   (doseq [[k v] (keyless-env-vars jwt (keyword world))]
                     (println (str "export " k "=\"" v "\""))))

      "init" (do (init-all-worlds!)
                 (println "Initialized" (count @ephemeral-keys) "world keys"))

      (do (println "Usage: ephemeral <command> [args]")
          (println "Commands: generate, status, status-all, rotate, rotate-all, env-vars, init")))))
