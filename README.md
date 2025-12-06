# linkseq

A list of links in Lisp. Static site generator for personal link aggregation.

## Overview

linkseq generates a single, self-contained HTML file from a Clojure script. Edit the script, run it, deploy the output.

## Installation

[Install Nix.](https://nixos.org/download/#nix-install-linux)

## Development

Enter the development environment:

```bash
./dev.sh
```

This drops you into a shell with `babashka`, `cljfmt`, and other tools. Then rebuild:

```bash
bb linkseq.clj
```

Output: `public/index.html`

## Build

To build without entering the shell:

```bash
nix build
```

Output: `result/index.html`

## Customization

Edit the `site` map in `linkseq.clj`:

```clojure
(def site
  {:title "Your Name"
   :avatar (h/raw (slurp "assets/avatar.txt"))
   :bio-lines [...]
   :links [...]
   :socials [...]})
```

Add links, change colors, modify layout; it's just Clojure.

## Tech

Built with [Hiccup](https://github.com/weavejester/hiccup) for HTML generation and [Babashka](https://babashka.org) for scripting.

## License

MIT
