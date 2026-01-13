# Data Dictionary

This document provides a description of the data files and their contents used in this project.

## Data Sources

The data is divided into three categories, each stored in its own directory:

*   `api_data_aadhar_biometric/`
*   `api_data_aadhar_demographic/`
*   `api_data_aadhar_enrolment/`

Each directory contains multiple CSV files that are concatenated for analysis.

### `api_data_aadhar_biometric`

This dataset contains information about biometric updates for Aadhaar.

| Column | Description | Data Type |
| --- | --- | --- |
| `date` | The date of the record. | string |
| `state` | The state where the record was made. | string |
| `district` | The district where the record was made. | string |
| `pincode` | The pincode of the location. | integer |
| `bio_age_5_17` | Number of biometric updates for the 5-17 age group. | integer |
| `bio_age_17_` | Number of biometric updates for the 17+ age group. | integer |

### `api_data_aadhar_demographic`

This dataset contains information about demographic updates for Aadhaar.

| Column | Description | Data Type |
| --- | --- | --- |
| `date` | The date of the record. | string |
| `state` | The state where the record was made. | string |
| `district` | The district where the record was made. | string |
| `pincode` | The pincode of the location. | integer |
| `demo_age_5_17` | Number of demographic updates for the 5-17 age group. | integer |
| `demo_age_17_` | Number of demographic updates for the 17+ age group. | integer |

### `api_data_aadhar_enrolment`

This dataset contains information about new Aadhaar enrolments.

| Column | Description | Data Type |
| --- | --- | --- |
| `date` | The date of the record. | string |
| `state` | The state where the record was made. | string |
| `district` | The district where the record was made. | string |
| `pincode` | The pincode of the location. | integer |
| `age_0_5` | Number of new enrolments for the 0-5 age group. | integer |
| `age_5_17` | Number of new enrolments for the 5-17 age group. | integer |
| `age_18_greater` | Number of new enrolments for the 18+ age group. | integer |
