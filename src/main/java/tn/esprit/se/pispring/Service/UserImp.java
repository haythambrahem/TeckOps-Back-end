package tn.esprit.se.pispring.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.se.pispring.DTO.Request.*;
import tn.esprit.se.pispring.DTO.Response.CurrentUserResponse;
import tn.esprit.se.pispring.DTO.Response.PageResponse;
import tn.esprit.se.pispring.DTO.Response.UserResponse;
import tn.esprit.se.pispring.Repository.RoleRepo;
import tn.esprit.se.pispring.Repository.UserRepository;
import tn.esprit.se.pispring.entities.ERole;
import tn.esprit.se.pispring.entities.Portfolio;
import tn.esprit.se.pispring.entities.Role;
import tn.esprit.se.pispring.entities.User;
import tn.esprit.se.pispring.secConfig.JwtUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class UserImp implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepo roleRepo;
    private final JwtUtils jwtUtils;

    @Override
    public String signup(UserSignupRequest userReq) throws Exception {
        try {
            User user = new User();
            user.setEmail(userReq.getEmail());
            user.setPassword(passwordEncoder.encode(userReq.getPassword()));
            Role role = roleRepo.findRoleByRoleName(ERole.ROLE_USER);
            List<Role> roles = Collections.singletonList(role);
            user.setRoles(roles);
            User  u = userRepository.save(user);
            return "Done";
        } catch (Exception e) {
            throw new Exception("error adding the new user");
        }

    }

    @Override
    public User findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public CurrentUserResponse getCurrentUserInfos(String token) throws Exception {
        try {
            User user = userRepository.findByEmail(jwtUtils.getUsernameFromToken(token.split(" ")[1].trim()));
            return new CurrentUserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail()
            );
        }catch (Exception e) {
            throw new Exception(e);

        }
    }

    @Override
    public CurrentUserResponse editCurrentUserInfos(String token, CurrentUserRequest request) throws Exception {
        try {
            User user = userRepository.findByEmail(jwtUtils.getUsernameFromToken(token.split(" ")[1].trim()));
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
                throw new Exception("incorrect password");
            }
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            User savedUser = userRepository.save(user);
            return new CurrentUserResponse(
                    savedUser.getId(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getEmail()
            );
        }catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public String editPassword(String token, EditPasswordRequest request) throws Exception {
        try {
            if (!request.getNewPassword().equals(request.getRetypedNewPassword())) {
                throw new Exception("passwords don't match");
            }
            User user = userRepository.findByEmail(jwtUtils.getUsernameFromToken(token.split(" ")[1].trim()));
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new Exception("incorrect password");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return "success";
        }catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public List<UserResponse> getUsers(String token) throws Exception {
        try {

            return userRepository.findUsers((Portfolio) userRepository.
                   findByEmail(jwtUtils.getUsernameFromToken(token.split(" ")[1].trim())).
                   getPortfolio()).stream().filter(User -> !User.getRoles().
                   contains(roleRepo.findRoleByRoleName(ERole.ROLE_ADMIN)))
                   .map(User -> new UserResponse(
                   ))
                    .collect(Collectors.toList());

        }catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public List<UserResponse> searchUsers(String token, SearchRequest searchRequest) throws Exception {
        try {
            return userRepository.searchUsers(searchRequest.getKeyword(), (Portfolio) userRepository
                   .findByEmail(jwtUtils.getUsernameFromToken(token.split(" ")[1].trim())).getPortfolio())
                   .stream().filter(appUser -> !appUser.getRoles().contains(roleRepo
                   .findRoleByRoleName(ERole.ROLE_ADMIN))).map(appUser -> new UserResponse(
                    )).collect(Collectors.toList());
        }catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public PageResponse<UserResponse> findAll(Long page, Long size, String criteria, String direction, String searchTerm) {

        Sort sort = Sort.by(Objects.equals(direction, "asc") ? Sort.Direction.ASC : Sort.Direction.DESC,criteria);

        PageRequest pageRequest = PageRequest.of(Math.toIntExact(page), Math.toIntExact(size), sort);
        Page<User> result;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            result = userRepository.findAll(pageRequest);
        }else {
            result = userRepository.searchBy(pageRequest, searchTerm);
        }


        PageResponse<UserResponse> response = new PageResponse<>();
        response.setContent(result.getContent().stream().map(user -> new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getTelephone())).toList());
        response.setPageNumber(result.getNumber());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(response.getTotalPages());

        return response;
    }

    @Override
    public UserResponse addUser(AddUserRequest request) {


        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setTelephone(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode("123456789"));

        User saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail(), saved.getTelephone());
    }
}
