package com.worfwint.tabletoprpgmanager.restcontroller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worfwint.tabletoprpgmanager.dto.response.UserPublicProfileResponse;
import com.worfwint.tabletoprpgmanager.services.UserService;


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
    public List<UserPublicProfileResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    
}
