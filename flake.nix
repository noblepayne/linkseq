{
  description = "linkseq - list of links in lisp";
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
    treefmt-nix.url = "github:numtide/treefmt-nix";
  };

  outputs = {
    self,
    nixpkgs,
    treefmt-nix,
  }: let
    supportedSystems = ["x86_64-linux" "aarch64-linux"];
    pkgsBySystem = nixpkgs.lib.getAttrs supportedSystems nixpkgs.legacyPackages;
    forAllPkgs = fn: nixpkgs.lib.mapAttrs (system: pkgs: (fn system pkgs)) pkgsBySystem;

    # treefmt configuration
    treefmtEval = forAllPkgs (
      system: pkgs:
        treefmt-nix.lib.evalModule pkgs {
          projectRootFile = "flake.nix";

          programs.alejandra.enable = true;
          programs.cljfmt.enable = true;
          programs.prettier.enable = true;
          programs.mdformat.enable = true;

          # Optional: configure prettier settings
          # settings.formatter.prettier.options = ["--tab-width" "2"];
        }
    );
  in {
    # nix fmt
    formatter = forAllPkgs (system: pkgs: treefmtEval.${system}.config.build.wrapper);

    # nix flake check - validates formatting
    checks = forAllPkgs (system: pkgs: {
      formatting = treefmtEval.${system}.config.build.check self;
    });

    packages = forAllPkgs (system: pkgs: {
      default = pkgs.callPackage ./package.nix {};
    });

    devShells = forAllPkgs (system: pkgs: {
      default = pkgs.mkShell {
        packages = [
          pkgs.babashka
          # treefmt wrapper includes all formatters
          treefmtEval.${system}.config.build.wrapper
        ];

        shellHook = ''
          if [ -n "$OLDSHELL" ]; then
            export SHELL=$OLDSHELL
          fi
        '';
      };
    });
  };
}
