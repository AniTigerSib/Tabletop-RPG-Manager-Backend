package com.worfwint.tabletop_rpg_manager.restcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worfwint.tabletop_rpg_manager.services.UserService;


/**
 *
 * @author michael
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getAllUsers() {
        return userService.getAllUsers().toString();
    }
    
}
