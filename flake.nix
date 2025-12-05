{
  description = "linkseq - list of links in lisp";
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = {
    self,
    nixpkgs,
  }: let
    supportedSystems = ["x86_64-linux" "aarch64-linux"];
    pkgsBySystem = nixpkgs.lib.getAttrs supportedSystems nixpkgs.legacyPackages;
    forAllPkgs = fn: nixpkgs.lib.mapAttrs (system: pkgs: (fn system pkgs)) pkgsBySystem;
  in {
    formatter = forAllPkgs (system: pkgs: pkgs.alejandra);
    packages = forAllPkgs (system: pkgs: {
      default = pkgs.callPackage ./package.nix {};
    });
    devShells = forAllPkgs (system: pkgs: {
      default = pkgs.mkShell {
        packages = [
          pkgs.babashka
          pkgs.cljfmt
          pkgs.nodePackages.prettier
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
