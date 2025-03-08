# quake-one-click

This program enables one-click installation and run of Quake 1 and 2 maps and mods.

Home page: [Quake One-Click Installer](https://jjelliott.github.io/quake-one-click)

Repository: [Github](https://github.com/jjelliott/quake-one-click)

## Link format

The program will register handlers for `q1package` and `q2package` links. All of the following information applies to both protocols.

For packages without dependencies:

```
q1package:{download-url},{type},{gamedir},{map-to-start?}
```

For packages with dependencies:

```
q1package:{download-url},{type},{gamedir},{parent-package-install-link},{map-to-start?}
```

| field                       | explanation                                                                 |
|-----------------------------|-----------------------------------------------------------------------------|
| download-url                | Url to download the resource                                                |
| type                        | see table below                                                             |
| gamedir                     | Gamedir to extract files to (ex. `id1`, `quoth`, `ad`)                      | 
| parent-package-install-link | One-click install link for required package, with commas replaced with `\|` | 
| map-to-start                | (Optional) map to start after completing installation.                      |

### Install types

| type                | explanation                                                                                                                                                         |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `root`/`mod-folder` | This type of package contains files that should be extracted directly to the Quake directory, ex. `sm228.zip` contains a single folder called `sm228`.              |
| `map`               | This type of package contains only files that should be placed within the `maps` directory of the applicable mod directory, ex. `.bsp`, `.lit`.                     |
| `gamedir`           | Contains a collection of assets intended to be extracted to a mod directory, ex. `quoth2pt2full_2.zip` contains `pak0.pak` - `pak2.pak` at the top level of the zip |
| `mod-map`           | This package type is identical to `map` but has a dependency on a mod package.                                                                                      |
| `mod-gamedir`       | This package type is identical to `gamedir` but has a dependency on a mod package.                                                                                  |

## Usage

### Download

Binaries will be available from the Releases section.

### Installation

#### Windows

Download the Windows package and extract it to the desired location.

Run the binary (`quake-one-click.exe`) and set up the configuration via menu. You must install the handler and set the
quake paths.

If you move `quake-one-click.exe` you _must_ re-run the handler installation.

#### Linux

Download the Linux package and extract it to a location on your path.

Run the binary from a terminal (`quake-one-click`) and set up the configuration via menu. You must install the handler and
set the quake paths.

Known incompatibilities:

- XWayland browsers will not launch the game. XOrg or native Wayland appear to work fine.
- You may need to set `XDG_CURRENT_DESKTOP`. `xdg-open q1package:asdf` will
  return `file 'q1package:asdf' does not exist` if this is the issue.
- Browsers installed via Snap do not allow custom URL handlers, use a natively installed browser instead. Not sure if
  Flatpak works.

## Development

### Building

Use GraalVM CE 17 as your JDK.

Run `./gradlew nativeCompile` to create a binary. Install the binary as you would a release version.

### Running tests

_Note: Tests are not yet implemented._

To run the test suite, run `./gradlew check`.

## Test links

`root`/`mod-folder` type:
[SM228 (via Slipseer)](https://jjelliott.github.io/http-protocol-redirector/?r=q1package:https://www.slipseer.com/index.php?resources/sm228-vanilla.335/download,mod-folder,sm228,start)

`map` type:
[Enforced Entropy (via Slipseer)](https://jjelliott.github.io/http-protocol-redirector/?r=q1package:https://www.slipseer.com/index.php?resources/enforced-entropy.343/download,map,id1,spasp1)

`gamedir` type:
[Quoth 2.2 (via Quaddicted)](https://jjelliott.github.io/http-protocol-redirector/?r=q1package:https://www.quaddicted.com/filebase/quoth2pt2full_2.zip,gamedir,quoth,start)

`mod-gamedir` type:
[Blood Colored Rust (via Quaddicted)](https://jjelliott.github.io/http-protocol-redirector/?r=q1package:https://www.quaddicted.com/filebase/mhsp01.zip,mod-gamedir,quoth,https://www.quaddicted.com/filebase/quoth2pt2full_2.zip|gamedir|quoth,mhsp01)

Quake 2 `map` type:
[Infiltrate :: Subjugate :: Eliminate (via Quaddicted)](https://jjelliott.github.io/http-protocol-redirector/?r=q2package:https://www.quaddicted.com/files/idgames2/quake2/levels/g-i/ise.zip,map,baseq2,ise)
