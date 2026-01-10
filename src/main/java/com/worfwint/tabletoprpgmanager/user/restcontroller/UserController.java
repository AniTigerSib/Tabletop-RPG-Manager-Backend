package com.worfwint.tabletoprpgmanager.user.restcontroller;

import com.worfwint.tabletoprpgmanager.user.dto.response.SelfUserProfile;
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

import com.worfwint.tabletoprpgmanager.common.dto.AuthenticatedUser;
import com.worfwint.tabletoprpgmanager.common.dto.response.PageResponse;
import com.worfwint.tabletoprpgmanager.user.dto.response.UserFullProfileResponse;
import com.worfwint.tabletoprpgmanager.user.dto.response.UserPublicProfileResponse;
import com.worfwint.tabletoprpgmanager.user.dto.response.UserSearchProfileResponse;
import com.worfwint.tabletoprpgmanager.common.exception.BadRequestException;
import com.worfwint.tabletoprpgmanager.common.exception.UnauthorizedException;
import com.worfwint.tabletoprpgmanager.user.exception.UserNotFoundException;
import com.worfwint.tabletoprpgmanager.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
    @Operation(
            summary = "List public user profiles",
            description = "Returns a paginated slice of user profiles that are visible to all clients. "
                    + "Use the optional page and size parameters to navigate through the entire result set."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Page of public profiles returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(
            summary = "Search users by username",
            description = "Performs a case-insensitive search using the provided username fragment and returns "
                    + "a paginated collection of matches. When the query parameter is omitted all users are returned."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Page of matching users returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/search")
    public PageResponse<UserSearchProfileResponse> searchUsers(
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
    @Operation(
            summary = "Get current user profile",
            description = "Returns the authenticated user's full profile including email and display name."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SelfUserProfile.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No authenticated user was provided",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/me")
    public SelfUserProfile getCurrentUserProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        return userService.getSelfUserProfile(authenticatedUser.id());
    }

    /**
     * Returns the public profile for the specified user identifier.
     *
     * @param userId user identifier
     * @return public profile DTO
     */
    @Operation(
            summary = "Get a user's public profile",
            description = "Returns the public information for the user identified by the supplied identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Public profile returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPublicProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The requested user could not be found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(
            summary = "Get a user's public profile by username",
            description = "Retrieves the public profile associated with the supplied username, ignoring case."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Public profile returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserPublicProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No user with the provided username exists",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(
            summary = "Get a user's full profile",
            description = "Returns the complete profile of the requested user. Access is limited to moderators, "
                    + "administrators, developers or the user whose profile is being requested."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Full profile returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserFullProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The caller is not allowed to access this profile",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The requested user could not be found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
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
