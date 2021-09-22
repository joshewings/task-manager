# task-manager
A software component that is designed for handling multiple processes inside an operating system.

## Running the application
Just run the Spring Boot app.
This is a command line application.  Use the "help" command to get a list of available commands.

## Configuration
The maximum allowed number of running processes can be configured in application.properties.
The value must be greater than 0.

## Next steps for this application
- Add unit tests.
- Enable integration testing.  There is some incompatibility between Sprint Boot integration testing and Spring Shell which needs to be resolved.
- Improve exception handling by creating an application-specific exception which can be caught in the shell, instead of catching all exceptions.

