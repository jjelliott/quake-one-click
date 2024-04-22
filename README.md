# q1-installer

This program enables one-click installation and run of Quake 1 maps and mods.

Repository: [Github](https://github.com/jjelliott/quake-one-click)

## Link format

```
q1package:{download-url},{type},{gamedir},{map-to-start?}
```

| field              | explanation                                            |
|--------------------|--------------------------------------------------------|
| download-url       | Url to download the resource                           |
| type               | `mod-folder`, `map` or `gamedir`, more info below      |
| gamedir            | Gamedir to extract files to (ex. `id1`, `quoth`, `ad`) | 
| map-to-start       | (Optional) map to start after completing installation. |

### Install types

| type       | explanation                                                                                                                                                         |
|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| mod-folder | This type of package contains a fully contained mod directory, ex. `sm228.zip` contains a single folder called `sm228`.                                             |
| map        | This type of package contains only files that should be placed within the `maps` directory of the applicable mod directory, ex. `.bsp`, `.lit`.                     |
| gamedir    | Contains a collection of assets intended to be extracted to a mod directory, ex. `quoth2pt2full_2.zip` contains `pak0.pak` - `pak2.pak` at the top level of the zip |


## Usage

### Download

Binaries will be available from the Releases section.

### Installation

#### Windows
Download the Windows package and extract it to the desired location.

Run the binary (`q1-installer.exe`) and set up the configuration via menu. You must install the handler and set the quake paths.

If you move `q1-installer.exe` you _must_ re-run the handler installation.

#### Linux
Install Java 17 or higher and download the Java package. Extract to desired location.

Run `java -jar /path/to/q1-installer.jar` and set up the configuration via menu. You must install the handler and set the quake paths.





## Development

### Building

Use GraalVM CE 17 as your JDK.

Run `./gradlew nativeCompile` to create a binary. Install the binary as you would a release version.

### Running tests

_Note: Tests are not yet implemented._

To run the test suite, run `./gradlew check`.

## Test links

[SM228 (via Slipseer)](q1package:https://www.slipseer.com/index.php?resources/sm228-vanilla.335/download,mod-folder,sm228,start)

[Enforced Entropy (via Slipseer)](q1package:https://www.slipseer.com/index.php?resources/enforced-entropy.343/download,map,id1,spasp1)

[Quoth 2.2 (via Quaddicted)](q1package:https://www.quaddicted.com/filebase/quoth2pt2full_2.zip,gamedir,quoth,start)