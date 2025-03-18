classDiagram
    Server <|-- User
    Server <|-- UserA
    Server <|-- UserB
    Server <|-- Message
    Server <|-- ChatRoom

       class Server{
      + int UserCount
      + int UsersOnline
      + String TimeStamp
    }
    class User{
      + String UserID // OU email
      + String UserPassword
      + String SentMessage
      + String ReceivedMessage
      + boolean IsPassTrue
      + boolean IsIDTrue
    }
 
    class UserA{
      + boolean MsgRead
      +
      +
    }
    class UserB{
        + boolean MsgRead
        +
        +
    }
    class Message{
        + String TimeStamp
        + String DlvrTime
        + String Contents
        + String MsgFailed
    }
    class ChatRoom{
        + String RoomName
        + String WhiteList
        + boolean IsPrivate
    }
