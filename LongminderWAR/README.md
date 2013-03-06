
Longminder Dynamic Web Application
----------------------------------

This application will handle web input from either Javaserver Faces input
or REST interface, and direct it to EJB backend.

The web application has two (or three) layers.
Top layer for JSF are classes LoginDTO, WebUserDTO, ModifyUser, ModifyAlert,
NewAlert, AlertList etc.
These classes are JSF backing beans that take the input or form values and
on action they pass them and the related HttpServletRequest to the bottom layer.

Top layer for REST handling are classes UserManagerRest, MyJacksonJsonProvider,
JsonResponse and ThrowableExceptionMapper.

Bottom layer consists of classes UserManagerService and AlertManagerService.
These classes take the input from top layer and direct the requested actions to
the EJB backend. They also take the Request instance so that they can authenticate
the user for use in the EJB backend.

Thus input validification is divided to the layers as follows:
- Top layer validifies the form input that isn't validified by JSF tags and
  can properly show error messages to the user.

- Bottom layer keeps sure that the user identification data sent to the
  EJB backend is actually correct data verified by Java session handling
  procedures.

