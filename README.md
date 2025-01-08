# COVID Contact Tracer

This project is a Java-based simulation of a COVID-19 contact tracing system, designed to model interactions between mobile devices and a government contact tracing entity.

## Features

- **Mobile Device Simulation**: Represents individual users recording contacts with others, reporting positive COVID-19 tests, and synchronizing data with a central system.

- **Government Contact Tracer**: Acts as the central authority receiving data from mobile devices, storing contact information, and identifying potential exposures.

- **Data Synchronization**: Implements data exchange between mobile devices and the government system, including hashing device information for privacy.

## Project Structure

- `MobileDevice.java`: Defines the `MobileDevice` class, managing user contacts, positive test reports, and data synchronization.

- `Government.java`: Defines the `Government` class, handling data received from mobile devices and processing contact information.

- `JUnit.java`: Contains unit tests for the `MobileDevice` and `Government` classes to ensure functionality.

- `config_fileforGovernment.txt`: Configuration file for initializing the government system.

- `MobileDevice1.txt` to `MobileDevice6.txt`: Configuration files for initializing six different mobile devices.

- `SQL.sql`: SQL script for setting up the necessary database schema to store contact tracing information.

- `Project report.pdf`: Detailed report explaining the project's design, implementation, and testing.

## Setup and Usage

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/DeekshaSareen-code/covid-contact-tracer.git
   ```

2. **Compile the Java Files**:

   ```bash
   javac MobileDevice.java Government.java JUnit.java
   ```

3. **Run the Unit Tests**:

   ```bash
   java JUnit
   ```

4. **Configure the System**:

   - Edit the `config_fileforGovernment.txt` to set up the government system's initial parameters.

   - Edit `MobileDevice1.txt` to `MobileDevice6.txt` to configure each mobile device's initial state.

5. **Initialize the Database**:

   - Use the `SQL.sql` script to create the necessary database tables.

6. **Run the Simulation**:

   - Instantiate the `Government` and `MobileDevice` classes in a main method or interactive environment.

   - Simulate interactions by calling methods to record contacts, report positive tests, and synchronize data.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
