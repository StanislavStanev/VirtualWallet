package com.team14.virtualwallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppCommandLineRunner implements CommandLineRunner {

//    private UsersService usersService;
//    private RolesRepository rolesRepository;
//    private UsersRepository usersRepository;
//    private UserProfilesRepository userProfilesRepository;
//    private BankCardsRepository bankCardsRepository;
//    private BankCardsService bankCardsService;

//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Autowired
//    public AppCommandLineRunner(UsersService usersService, RolesRepository rolesRepository, UsersRepository usersRepository,BankCardsRepository bankCardsRepository,BankCardsService bankCardsService) {
//        this.usersService = usersService;
//        this.rolesRepository = rolesRepository;
//        this.usersRepository = usersRepository;
//        this.bankCardsRepository = bankCardsRepository;
//        this.bankCardsService = bankCardsService;
//    }

    //@Override
    //@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Insufficient funds")
    public void run(String... args) throws JsonProcessingException {
//        seedUser();
//        seedRoles();
//        seedUserRole();
//        seedBankCard();
//        System.out.println("success");
//        String md5Hex = DigestUtils
////                .md5Hex("rarnaudovtest100");
////        System.out.println(md5Hex);
    }

//    private void seedUserRole() {
//        User user = this.usersRepository.findByUsername("testUser").orElse(null);
//        Role role = this.rolesRepository.findAll().get(0);
//        Set<Role> roles = new HashSet<>();
//        roles.add(role);
//
//        user.setAuthorities(roles);
//        user.setBlocked(false);
//        this.usersRepository.save(user);
//    }
//
//
//    private void seedRoles() {
//        Role role = new Role();
//        role.setAuthority("ADMIN");
//        rolesRepository.save(role);
//
//        role = new Role();
//        role.setAuthority("USER");
//        rolesRepository.save(role);
//
//        role = new Role();
//        role.setAuthority("ROOT");
//        rolesRepository.save(role);
//    }
//
//    private void seedUser() {
//        RegisterUserDto registerUserDto = new RegisterUserDto();
//        registerUserDto.setEmail("test@test");
//        registerUserDto.setPassword("aaaaaa");
//        registerUserDto.setUsername("testUser");
//
//        this.usersService.register(registerUserDto);
//
//    }
//
//    private void seedBankCard() {
//        RegisterBankCardDto registerBankCardDto = new RegisterBankCardDto();
//        registerBankCardDto.setExpirationDateYear("20");
//        registerBankCardDto.setExpirationDateMonth("12");
//        registerBankCardDto.setCardNumber("1234-1234-1234-1234");
//        registerBankCardDto.setCardHolderName("Radi Arnaudov");
//        registerBankCardDto.setCsv("123");
//        registerBankCardDto.setCardIssuer("Bank");
//        bankCardsService.register(registerBankCardDto,"rarnaudov");
//    }
}
