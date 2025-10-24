package com.worfwint.tabletoprpgmanager.restcontroller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worfwint.tabletoprpgmanager.dto.AuthenticatedUser;
import com.worfwint.tabletoprpgmanager.dto.response.PageResponse;
import com.worfwint.tabletoprpgmanager.dto.response.UserFullProfileResponse;
import com.worfwint.tabletoprpgmanager.dto.response.UserPublicProfileResponse;
import com.worfwint.tabletoprpgmanager.dto.response.UserSearchInfoResponse;
import com.worfwint.tabletoprpgmanager.exception.BadRequestException;
import com.worfwint.tabletoprpgmanager.exception.UnauthorizedException;
import com.worfwint.tabletoprpgmanager.exception.UserNotFoundException;
import com.worfwint.tabletoprpgmanager.services.UserService;

/**
 * REST controller exposing endpoints for retrieving user profile information.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final UserService userService;

    /**
     * Creates the controller with its required dependencies.
     *
     * @param userService service that provides user related operations
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns a paginated list of users for public display.
     *
     * @param page page index to return
     * @param size requested page size
     * @return page of public profile summaries
     */
    @GetMapping({"", "/"})
    public PageResponse<UserPublicProfileResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = buildPageRequest(page, size);
        return userService.getUsers(pageable);
    }

    /**
     * Searches for users using a username fragment.
     *
     * @param query optional search term
     * @param page page index to return
     * @param size requested page size
     * @return page of search results
     */
    @GetMapping("/search")
    public PageResponse<UserSearchInfoResponse> searchUsers(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = buildPageRequest(page, size);
        return userService.searchUsers(query, pageable);
    }

    /**
     * Returns the full profile of the currently authenticated user.
     *
     * @param authenticatedUser current user injected by Spring Security
     * @return full profile DTO
     */
    @GetMapping("/me")
    public UserFullProfileResponse getCurrentUserProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        return userService.getFullUserProfile(authenticatedUser.getId());
    }

    /**
     * Returns the public profile for the specified user identifier.
     *
     * @param userId user identifier
     * @return public profile DTO
     */
    @GetMapping("/{userId}")
    public UserPublicProfileResponse getUserPublicProfile(@PathVariable Long userId) {
        return userService.getPublicUserProfile(userId);
    }

    /**
     * Returns the public profile for the specified username.
     *
     * @param username username to lookup
     * @return public profile DTO
     */
    @GetMapping("/by-username/{username}")
    public UserPublicProfileResponse getUserPublicProfileByUsername(@PathVariable String username) {
        return userService.getPublicUserProfileByUsername(username);
    }

    /**
     * Returns the full profile for the specified user identifier.
     * Access is restricted to privileged roles or the user themselves.
     *
     * @param userId user identifier
     * @return full profile DTO
     */
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN','DEVELOPER') or #userId == principal.id")
    @GetMapping("/{userId}/full")
    public UserFullProfileResponse getUserFullProfile(@PathVariable Long userId) {
        return userService.getFullUserProfile(userId);
    }

    /**
     * Builds a pageable object enforcing bounds on the page and size parameters.
     *
     * @param page requested page index
     * @param size requested page size
     * @return {@link Pageable} configured with sane defaults
     */
    private Pageable buildPageRequest(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page index must be greater or equal to 0");
        }
        if (size <= 0) {
            throw new BadRequestException("Page size must be greater than 0");
        }
        int cappedSize = Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(page, cappedSize, Sort.by("username").ascending());
    }

    /**
     * Handles situations where the requested user cannot be found.
     *
     * @param ex exception describing the failure
     * @return {@code 404 Not Found} response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles validation errors in the request payload or query parameters.
     *
     * @param ex exception describing the validation error
     * @return {@code 400 Bad Request} response
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles requests performed without sufficient authentication.
     *
     * @param ex exception describing the unauthorized access
     * @return {@code 401 Unauthorized} response
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
