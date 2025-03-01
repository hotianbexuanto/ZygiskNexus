# Zygisk Nexus

Standalone implementation of Zygisk, providing Zygisk API support for KernelSU and Magisk. This is a fork of Zygisk Next with enhanced features.

## Features

+ Support for both KernelSU and Magisk simultaneously
+ Priority given to Magisk when both root solutions are present
+ Full Zygisk API compatibility
+ Standalone implementation without requiring built-in Zygisk

## Requirements

### KernelSU

+ Minimal KernelSU version: 10940
+ Minimal KernelSU Manager (ksud) version: 11424
+ Kernel has full SELinux patch support

### Magisk

+ Minimal version: 26402
+ Built-in Zygisk turned off

## Compatibility

`PROCESS_ON_DENYLIST` cannot be flagged correctly for isolated processes on Magisk DenyList currently.

Zygisk Nexus only guarantees the same behavior of Zygisk API, but will NOT ensure Magisk's internal features.
