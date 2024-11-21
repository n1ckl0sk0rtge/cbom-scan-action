# cbomkit-action

GitHub Action to generate CBOM.

## Usage

```yaml
on:
  workflow_dispatch:

jobs:
  cbom-scan:
    runs-on: ubuntu-latest
    name: CBOM generation
    permissions:
      contents: write
      pull-requests: write
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
      - name: Create CBOM
        uses: n1ckl0sk0rtge/cbomkit-action@v0.0.18
        id: cbom
      # Allow you to persist CBOM after a job has completed, and share 
      # that CBOM with another job in the same workflow.
      - name: Create and publish CBOM artifact
        uses: actions/upload-artifact@v4
        with:
          name: "CBOM"
          path: ${{ steps.cbom.outputs.filename }}
      # Create a PR to merge the created CBOM
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
