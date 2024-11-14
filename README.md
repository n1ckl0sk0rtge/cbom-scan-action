# About

GitHub Action to generate CBOM.

# Usage

```yaml
on:
  push:
  workflow_dispatch:

jobs:
  cbom-scan:
    runs-on: ubuntu-latest
    name: CBOM generation
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
      - name: cbom scan
        uses: n1ckl0sk0rtge/cbom-scan-action@v0.0.8
```