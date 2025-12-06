{
  stdenv,
  babashka,
  lib,
  ...
}:
stdenv.mkDerivation {
  pname = "linkseq";
  version = "0.1.0";

  src = ./.;

  dontConfigure = true;

  buildPhase = let
    bbBin = lib.getExe babashka;
  in ''
    runHook preBuild

    ${bbBin} linkseq.clj

    runHook postBuild
  '';

  installPhase = ''
    runHook preInstall

    mv public $out

    runHook postInstall
  '';
}
