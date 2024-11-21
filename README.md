# cbom-scan-action

GitHub Action to generate CBOM.

## Usage

```yaml
on:
  workflow_dispatch:

jobs:
  cbom-scan:
    runs-on: ubuntu-20.04
    name: CBOM generation
    permissions:
      contents: write
      pull-requests: write
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
      - name: Create CBOM
        uses: n1ckl0sk0rtge/cbom-scan-action@v0.0.16
      - name: Commit changes to new branch
        run: |
          git config user.name github-actions
          git config user.email github-actions@github.com
          git switch -c security/update-cbom
          git add .
          git commit -m "Update CBOM"
          git push origin security/update-cbom
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      - name: create pull request
        run: gh pr create --base main --head security/update-cbom --title 'Update CBOM' --body 'CBOM created by GitHub Action'
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```
