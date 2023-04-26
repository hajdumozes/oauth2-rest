### Prerequisites

- set up a local keycloak server
    - create realm
    - create user with password
    - create client with authentication and authorization enabled (it will create a client secret)
    - attach the created entities together with assigning
- set keycloak configurations in the `application.yml`

### Environment variables

| Name                       | Format | Default value      | Comment                                                                                                              |
|----------------------------|--------|--------------------|----------------------------------------------------------------------------------------------------------------------|
| `SERVER_PORT`              | string | 8081               |                                                                                                                      |
| `CLIENT_ID`                | string |                    |                                                                                                                      |
| `CLIENT_SECRET`            | string |                    | can be claimed after setting the client to have authentication and authorization (making it not public)              |
| `AUTHORIZATION_URI`        | string |                    | Authorization URI of auth server, pattern: {server}/realms/{realm}/protocol/openid-connect/auth                      |
| `TOKEN_URI`                | string |                    | Get Access token URI, pattern: {server}/realms/{realm}/protocol/openid-connect/token                                 |
| `USER_INFO_URI`            | string |                    | URI to get user-info -> scopes, roles, attributes, pattern: {server}/realms/{realm}/protocol/openid-connect/userinfo |
| `USER_NAME_ATTRIBUTE`      | string | preferred_username | auth name is parsed from this attribute                                                                              |
| `SCOPE`                    | string | openid             |                                                                                                                      |
| `AUTHORIZATION_GRANT_TYPE` | string | password           |                                                                                                                      |
| `LOG_LEVEL`                | string | DEBUG              |                                                                                                                      |

### Usage

The controller endpoints can be called with basic auth, where the user name and password corresponds to the one set in
the realm