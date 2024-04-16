# q1-installer
This program enables one-click installation and run of Quake 1 maps and mods.

## Link format


## Usage

### Download
Binaries will be available from the Releases section.

### Installation
Obtain the appropriate binary for your system. 

On Linux, place the binary on your path. On Windows, place the binary where you want it.

Run the binary and set up the configuration via menu. You must install the handler and set the quake paths.

Once setup, one-click links should work.

Windows users: If you move `q1-installer.exe` you _must_ re-run the handler installation.

## Development
### Building
Use GraalVM CE 17 as your JDK.

Run `./gradlew nativeCompile` to create a binary. Install the binary as you would a release version.
### Running tests
_Note: Tests are not yet implemented._

To run the test suite, run `./gradlew check`. 
