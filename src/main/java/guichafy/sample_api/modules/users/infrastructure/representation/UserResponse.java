package guichafy.sample_api.modules.users.infrastructure.representation;

import guichafy.sample_api.modules.users.domain.Address;
import guichafy.sample_api.modules.users.domain.Company;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@Relation(collectionRelation = "users", itemRelation = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "username", "email", "phone", "website", "address", "company"})
public class UserResponse extends RepresentationModel<UserResponse> {

    private Long id;
    private String name;
    private String username;
    private String email;
    private Address address; // Pode ser um AddressResponse DTO se necessário
    private String phone;
    private String website;
    private Company company; // Pode ser um CompanyResponse DTO se necessário

    // Construtor, Getters e Setters
    public UserResponse() {}

    public UserResponse(Long id, String name, String username, String email, Address address, String phone, String website, Company company) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.company = company;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
}