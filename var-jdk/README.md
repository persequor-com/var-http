<h2 align="center"><b>Var-HTTP JDK</b></h2>

<h4 align="center">JDK based implementation of var-http.</h4>

**Deprecated** because some of the following:
- Utilizes http server from `com.sun.net.httpserver` (private) package.
- Servlet support is implemented in this module and not necessarily follows all the standards.
- Has some bugs and performance drawbacks comparing to other implementations (Undertow based).