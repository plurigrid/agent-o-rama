# Aptos Improvement Proposals Relevant to Aptos Society

> GF(3)-conserved reference document for the Aptos Society open game implementation.
> 
> **Trit Sum**: 9 CRITICAL + 6 IMPORTANT + 6 ENHANCEMENT = 21 AIPs
> **GF(3) Balance**: 21 ≡ 0 (mod 3) ✓

## Overview

Aptos Society implements a compositional open game with:
- **28 players**: 26 world agents (a-z) + Alice + Bob + Vault NPC
- **Diegetic Nash equilibria** via feedback propagation
- **Vertical lenses**: Kernel↔Bus, Goblins↔Kernel, DuckDB↔Bus
- **Settlement semantics**: withdraw all → vault → agents (conservation)
- **Gay.jl identity root** binding end-to-end

---

## 🔴 CRITICAL AIPs (Must Implement)

These AIPs are **required** for Aptos Society to function correctly.

### AIP-21: Fungible Assets
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-21.md |
| **Authors** | lightmark, movekevin, davidiw |
| **Society Relevance** | Core token standard for APT transfers between 28 wallets. Settlement semantics require FA operations. |
| **Trit** | +1 (PLUS) - Generative |

### AIP-12: Multisig Accounts v2
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-12.md |
| **Authors** | movekevin |
| **Society Relevance** | Required for multi-agent vault control. Enables K-of-N threshold for Society treasury. |
| **Trit** | 0 (ERGODIC) - Coordinating |

### AIP-77: Multisig V2 Enhancement
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-77.md |
| **Authors** | junkil-park, movekevin |
| **Society Relevance** | Enhanced multisig for NPC/Oracle operations. Supports complex approval flows. |
| **Trit** | -1 (MINUS) - Validating |

### AIP-100: Private Entry Function for Multisig Account Creation
| Field | Value |
|-------|-------|
| **Category** | Framework |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-100.md |
| **Authors** | yeptos, gregnazario |
| **Society Relevance** | Secure multisig creation for world agent wallets without public exposure. |
| **Trit** | +1 (PLUS) - Generative |

### AIP-55: Generalize Transaction Authentication and Support Arbitrary K-of-N MultiKey Accounts
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-55.md |
| **Authors** | davidiw, hariria |
| **Society Relevance** | Arbitrary K-of-N enables flexible agent quorum requirements. Essential for GF(3)-balanced triadic voting. |
| **Trit** | 0 (ERGODIC) - Coordinating |

### AIP-104: Account Abstraction
| Field | Value |
|-------|-------|
| **Category** | Framework |
| **Status** | In Review |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-104.md |
| **Authors** | igor-aptos |
| **Society Relevance** | Move-based authentication enables custom verification logic for AI agents. Critical for agent autonomy. |
| **Trit** | -1 (MINUS) - Validating |

### AIP-113: Domain-based Account Abstraction
| Field | Value |
|-------|-------|
| **Category** | Framework |
| **Status** | In Review |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-113.md |
| **Authors** | igor-aptos, lightmark |
| **Society Relevance** | Domain-scoped AA allows world agents to have distinct authentication domains. |
| **Trit** | +1 (PLUS) - Generative |

### AIP-11: Digital Assets: Tokens as Objects
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-11.md |
| **Authors** | davidiw, movekevin, lightmark, capcap, kslee8224, neoul |
| **Society Relevance** | Object model for capability tokens, skill mints, and Gay.jl identity anchors. |
| **Trit** | 0 (ERGODIC) - Coordinating |

### AIP-3: Multi-step Governance Proposal
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-3.md |
| **Authors** | 0xchloe |
| **Society Relevance** | Multi-step governance enables Nash equilibrium discovery through iterative voting rounds. |
| **Trit** | -1 (MINUS) - Validating |

---

## 🟡 IMPORTANT AIPs (Should Implement)

These AIPs **enhance** Aptos Society capabilities.

### AIP-63: Coin to Fungible Asset Migration
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-63.md |
| **Authors** | lightmark, davidiw, movekevin |
| **Society Relevance** | Migration path for legacy coin holdings to FA standard. |
| **Trit** | +1 (PLUS) |

### AIP-70: Parallelize Fungible Assets
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-70.md |
| **Authors** | igor-aptos, vusirikala |
| **Society Relevance** | Parallel FA operations enable concurrent 28-agent settlements. Critical for throughput. |
| **Trit** | 0 (ERGODIC) |

### AIP-61: Keyless Accounts
| Field | Value |
|-------|-------|
| **Category** | Blockchain |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-61.md |
| **Authors** | alinush |
| **Society Relevance** | OAuth/OIDC integration for agent identity. Enables human-verifiable agent authentication. |
| **Trit** | -1 (MINUS) |

### AIP-75: Prover Service for Keyless Accounts
| Field | Value |
|-------|-------|
| **Category** | Cryptography |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-75.md |
| **Authors** | Rex Fernando |
| **Society Relevance** | ZK proofs for keyless auth. Privacy-preserving agent verification. |
| **Trit** | +1 (PLUS) |

### AIP-66: Passkey Accounts
| Field | Value |
|-------|-------|
| **Category** | Cryptography |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-66.md |
| **Authors** | hariria |
| **Society Relevance** | WebAuthn-based agent authentication for browser-based game interfaces. |
| **Trit** | 0 (ERGODIC) |

### AIP-62: Wallet Standard
| Field | Value |
|-------|-------|
| **Category** | Devex |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-62.md |
| **Authors** | 0xmaayan, hardsetting, NorbertBodziony |
| **Society Relevance** | Standard wallet interface for MCP wallet tools integration. |
| **Trit** | -1 (MINUS) |

---

## 🟢 ENHANCEMENT AIPs (Nice to Have)

These AIPs provide **optional enhancements**.

### AIP-109: Hide Unwanted Soulbound Objects
| Field | Value |
|-------|-------|
| **Category** | Framework |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-109.md |
| **Authors** | gregnazario |
| **Society Relevance** | Hide obsolete skill tokens from agent wallets. UX improvement. |
| **Trit** | +1 (PLUS) |

### AIP-110: Lower Governance Proposal Threshold (400M → 300M APT)
| Field | Value |
|-------|-------|
| **Category** | Governance |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-110.md |
| **Authors** | sherry-x |
| **Society Relevance** | Lower threshold increases accessibility for Society governance proposals. |
| **Trit** | 0 (ERGODIC) |

### AIP-112: Function Values in the Move VM
| Field | Value |
|-------|-------|
| **Category** | Standard Language |
| **Status** | In Review |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-112.md |
| **Authors** | Wolfgang Grieskamp |
| **Society Relevance** | First-class functions enable more expressive open game strategies in Move. |
| **Trit** | -1 (MINUS) |

### AIP-83: Framework-level Untransferable Fungible Asset Stores
| Field | Value |
|-------|-------|
| **Category** | Framework |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-83.md |
| **Authors** | davidiw |
| **Society Relevance** | Soulbound tokens for agent capabilities that cannot be transferred. |
| **Trit** | +1 (PLUS) |

### AIP-76: Digital Assets Composability
| Field | Value |
|-------|-------|
| **Category** | Smart Contract |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-76.md |
| **Authors** | aladeenb, jczhang |
| **Society Relevance** | Composable NFTs for nested skill hierarchies and agent capabilities. |
| **Trit** | 0 (ERGODIC) |

### AIP-101: Safe Onchain Key Rotation Address Mapping
| Field | Value |
|-------|-------|
| **Category** | Framework |
| **Status** | Accepted |
| **URL** | https://github.com/aptos-foundation/AIPs/blob/main/aips/aip-101.md |
| **Authors** | Alex Kahn |
| **Society Relevance** | Safe key rotation for long-lived agent accounts. Security enhancement. |
| **Trit** | -1 (MINUS) |

---

## GF(3) Conservation Verification

```
CRITICAL (9 AIPs):
  AIP-21:  +1
  AIP-12:   0
  AIP-77:  -1
  AIP-100: +1
  AIP-55:   0
  AIP-104: -1
  AIP-113: +1
  AIP-11:   0
  AIP-3:   -1
  Sum: (+1+0-1+1+0-1+1+0-1) = 0 ✓

IMPORTANT (6 AIPs):
  AIP-63:  +1
  AIP-70:   0
  AIP-61:  -1
  AIP-75:  +1
  AIP-66:   0
  AIP-62:  -1
  Sum: (+1+0-1+1+0-1) = 0 ✓

ENHANCEMENT (6 AIPs):
  AIP-109: +1
  AIP-110:  0
  AIP-112: -1
  AIP-83:  +1
  AIP-76:   0
  AIP-101: -1
  Sum: (+1+0-1+1+0-1) = 0 ✓

TOTAL: 0 + 0 + 0 = 0 ≡ 0 (mod 3) ✓ CONSERVED
```

---

## Integration with Society Components

### Move Contracts (.topos/GayMove/)

| AIP | Contract Impact |
|-----|-----------------|
| AIP-21 | `multiverse.move`: Use `fungible_asset` module for APT operations |
| AIP-12/77/100 | `society_run.move`: Multisig for vault withdrawals |
| AIP-11 | Object-based capability tokens and identity anchors |
| AIP-104/113 | Future: Custom authentication for autonomous agents |

### TypeScript Kernel (src/opengame/)

| AIP | Code Impact |
|-----|-------------|
| AIP-55 | `types.ts`: Support K-of-N signature verification |
| AIP-62 | MCP wallet tools via standard interface |
| AIP-70 | Parallel settlement in `just-play.ts` |

### Vertical Lenses

| Lens | Relevant AIPs |
|------|---------------|
| Kernel ↔ Bus | AIP-70 (parallel reads) |
| Goblins ↔ Kernel | AIP-104 (AA capabilities) |
| DuckDB ↔ Bus | AIP-21 (FA state tracking) |

---

## References

- [Full AIP Index](https://github.com/aptos-foundation/AIPs/wiki/Index-of-AIPs)
- [Aptos Governance Portal](https://governance.aptosfoundation.org/)
- [Aptos Move Framework](https://github.com/aptos-labs/aptos-core/tree/main/aptos-move/framework)

---

*Generated for Aptos Society open game implementation.*
*GF(3) balanced across CRITICAL/IMPORTANT/ENHANCEMENT tiers.*
*Last updated: 2026-01-01*
