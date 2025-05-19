classDiagram
    direction LR

    package "Adapters_In_Driving" { %% Modificado para teste: sem parênteses
        class UserController {
            +UserUseCase userUseCase
            +getAllUsers() ResponseEntity~ApiResponse~
            +getUserById(Long id) ResponseEntity~ApiResponse~
            -toUserResponse(User user) UserResponse
        }
        class TodoController {
            +TodoUseCase todoUseCase
            +getTodosByUserId(Long userId) ResponseEntity~ApiResponse~
            +getTodoByIdAndUserId(Long todoId, Long userId) ResponseEntity~ApiResponse~
            -toTodoResponse(Todo todo) TodoResponse
        }
    }

    package "Application (Ports In & Use Cases)" {
        class UserUseCase {
            <<Interface>>
            +getAllUsers() List~User~
            +getUserById(Long id) Optional~User~
        }
        class UserServiceImpl {
            -UserPort userPort
            +getAllUsers() List~User~
            +getUserById(Long id) Optional~User~
        }
        UserServiceImpl ..|> UserUseCase : implements
        UserServiceImpl ..> UserPort : uses

        class TodoUseCase {
            <<Interface>>
            +findTodosByUserId(Long userId) List~Todo~
            +findTodoByIdAndUserId(Long todoId, Long userId) Todo
        }
        class TodoServiceImpl {
            -TodoPort todoPort
            +findTodosByUserId(Long userId) List~Todo~
            +findTodoByIdAndUserId(Long todoId, Long userId) Todo
        }
        TodoServiceImpl ..|> TodoUseCase : implements
        TodoServiceImpl ..> TodoPort : uses
    }

    package "Domain" {
        class User {
            -Long id
            -String name
            -Address address
            -Company company
        }
        class Todo {
            -Long id
            -Long userId
            -String title
            -Boolean completed
        }
        class Address
        class Company
        User "1" *-- "0..1" Address
        User "1" *-- "0..1" Company
    }

    package "Adapters Out (Driven & Ports Out)" {
        class UserPort {
            <<Interface>>
            +fetchAllUsers() List~User~
            +fetchUserById(Long id) Optional~User~
        }
        class UserAdapter {
            -RestClient restClient
            +fetchAllUsers() List~User~
            +fetchUserById(Long id) Optional~User~
        }
        UserAdapter ..|> UserPort : implements
        UserAdapter ..> RestClient : uses

        class TodoPort {
            <<Interface>>
            +findByUserId(Long userId) List~Todo~
            +findByIdAndUserId(Long todoId, Long userId) Optional~Todo~
        }
        class TodoAdapter {
            -RestClient restClient
            +findByUserId(Long userId) List~Todo~
            +findByIdAndUserId(Long todoId, Long userId) Optional~Todo~
        }
        TodoAdapter ..|> TodoPort : implements
        TodoAdapter ..> RestClient : uses
    }

    package "Infrastructure/External" {
        class RestClient {
            <<Framework Component>>
        }
        class JsonPlaceholderAPI {
            <<External Service>>
        }
    }

    package "Representations (DTOs)" {
        class UserResponse
        class TodoResponse
        class ApiResponse~T~
    }

    ' Relationships between packages
    UserController ..> UserUseCase
    UserController ..> UserResponse
    UserController ..> ApiResponse
    TodoController ..> TodoUseCase
    TodoController ..> TodoResponse
    TodoController ..> ApiResponse

    UserAdapter ..> User : (fetches/returns)
    TodoAdapter ..> Todo : (fetches/returns)
    UserAdapter ..> JsonPlaceholderAPI : (calls)
    TodoAdapter ..> JsonPlaceholderAPI : (calls)

    UserResponse -- User : (maps from)
    TodoResponse -- Todo : (maps from)
