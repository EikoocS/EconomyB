---

# EconomyB: A Modern, Modular Economy Plugin

> ⚠ **Warning**
> EconomyB is in the early stages of development. It is currently **unstable** and **not guaranteed to function correctly**. Use it **only** for **ontological debugging** and experimentation.

---

## What is EconomyB?

**EconomyB** is a modern, modular economy plugin designed with the following in mind:

* Support for multiple coexisting economies
* Asynchronous and responsive architecture
* Database-driven persistence

---

## Key Features

* ✅ Multi-economy support
* ✅ Asynchronous design
* ✅ Transaction system
* ✅ Database storage integration

---

## Installation Guide

The plugin is composed of three distinct modules:

* **`economyb`**
  The core module. Implements database connectivity and business logic. *Note: This module does not provide user-facing commands or APIs.*

* **`economyb-api`**
  The intermediate API layer. Provides an abstract interface (similar to VaultAPI), designed for use by other plugins. Also includes basic generic command support.

* **`economyb-api2vault`**
  A bridge module that maps `economyb-api` to VaultAPI, allowing Vault-compatible plugins (e.g., shops) to interact seamlessly with EconomyB.

> 💡 **Recommendation**
> Install **all three modules** unless you explicitly do not require VaultAPI compatibility.

To install, simply place the JAR files into your server’s `plugins/` directory.

---

## Roadmap / TODO

### 🔴 High Priority

* [ ] Implement user-facing commands (e.g., transfers), with permission configuration
* [ ] Support configuration of database connections
* [ ] Support configuration of the `api2vault` bridge
* [ ] Ensure comprehensive stability and robustness across all modules
* [ ] Improve documentation and provide a user-friendly tutorial

### 🟡 Medium Priority

* [ ] Implement a messaging system with full formatting and i18n (internationalization) support
* [ ] Add extensive logging functionality

### 🟢 Low Priority

* [ ] Provide support for VaultAPI's Bank module

---
