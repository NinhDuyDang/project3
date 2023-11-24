package com.example.project3.controller;

import com.example.project3.entity.User;
import com.example.project3.repository.UserRepository;
import com.example.project3.service.EmailSenderService;
import com.example.project3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
//@RestController
@RequestMapping(path = "")
public class RegisterController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("login")
    private String getLoginForm() {
        System.out.println("trang login");
        return "login";
    }

    /**
     * Tại trang đăng kí
     *
     * @param modelMap
     * @return form trang đăng kí
     */
    @GetMapping(path = "register")
    public String getFormRegister(ModelMap modelMap) {
        modelMap.addAttribute("user", new User());
        System.out.println("register");
        return "register";
    }

    /**
     * Tại trang đăng kí
     * Đăng kí người dùng và gửi mail
     *
     * @param user
     * @return trang login
     */
    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") User user, ModelMap modelMap) {
        Optional<User> emailAlreadyExist = userService.findUserByEmail(user.getEmail());
        if (emailAlreadyExist != null) {
            modelMap.addAttribute("emailAlreadyExist", new String());
            return "register";
        }
        System.out.println(user.toString());
        userService.register(user);
        System.out.println("them thanh cong user");
        return "redirect:/login";
    }

    /**
     * chuyển hướng đến trang ve thông tin xác thực email
     * <p>
     * Việc sử dụng 2 hàm này (confirmToken và confirmed) giúp ẩn tham số trên đường dẫn
     * Xem thêm: https://stackoverflow.com/questions/69016574/how-to-hide-model-data-from-url-in-thymeleaf/69017367#69017367
     *
     * @param token
     * @param redirectAttributes
     * @return đường dẫn trang confirmed
     */
    @GetMapping(path = "register/confirm")
    public String confirmToken(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        System.out.println("bấm vào link trong email gọi toi hàm này");
        String status = userService.confirm(token);
        redirectAttributes.addFlashAttribute("status", status);
        return "redirect:/confirmed";
    }

    /**
     * Tại trang confirmed
     * Nhận trạng thái xác thực
     *
     * @param status
     * @param modelMap
     * @return trang trạng thái thành công hay xác thực lỗi
     */
    @GetMapping(path = "confirmed")
    public String confirmed(@ModelAttribute("status") String status, ModelMap modelMap) {
        System.out.println("trang sau khi chọn xác thực email ẩn tham số đường dẫn");
        modelMap.addAttribute("status", status);
        return "confirmed";
    }

    /**
     * Tại trang nhập email: enteremail
     *
     * @return form trang nhập mail
     */
    @GetMapping("reset_password")
    private String getEnterEmailForm(ModelMap modelMap) {
        String email = new String();
        modelMap.addAttribute("email", email);
        System.out.println("trang nhập email");
        return "enteremail";
    }

    /**
     * Tại trang nhập email: enteremail
     * kiểm tra mail, gửi mail
     *
     * @return trang nhập email kèm thông bao lỗi nếu không tìm thấy email
     * @return trang xác nhận gửi mail thành công
     */
    @PostMapping("reset_password")
    private String resetPassword(@ModelAttribute(name = "email") String email, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findUserByEmail(email);
        if (user == null) {
            String emailNotFound = "emailNotFound";
            redirectAttributes.addFlashAttribute("emailNotFound", emailNotFound);
            return "redirect:/reset_password";
        }



        String link = "http://localhost:8080/change_password?email=" + email;
        String content = "Nhấp vào <a href=\"" + link + "\">liên kết này</a> để thay đổi mật khẩu";
        emailSenderService.send(
                email,
                content);
        System.out.println("trang xác nhận đã gửi mail thành công");
        return "forgotpassword";
    }

    /**
     * chuyển hướng đến trang nhập mật khẩu
     *
     * @param email
     * @param redirectAttributes
     * @return đường dẫn trang nhập mật khẩu
     */
    @GetMapping("change_password")
    private String getChangePasswordForm(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        System.out.println("trang nhập đổi mật khẩu");
        System.out.println(email);
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/enter_password";
    }

    /**
     * Tại trang nhập mật khẩu
     *
     * @param email
     * @param modelMap
     * @return
     */
    @GetMapping("enter_password")
    private String changePassword(@ModelAttribute(name = "email") String email, ModelMap modelMap) {
        System.out.println("trang đổi mật khẩu thành công trả về trang login");
        System.out.println(email);
        modelMap.addAttribute("email", email);
        String password = new String();
        String rePassword = new String();
        modelMap.addAttribute("password", password);
        modelMap.addAttribute("rePassword", rePassword);
        return "enterpassword";
    }

    @PostMapping("enter_password")
    private String changePassword(@ModelAttribute(name = "email") String email,
                                  @ModelAttribute(name = "password") String password,
                                  @ModelAttribute(name = "rePassword") String rePassword,
                                  ModelMap modelMap) {
        if (password.equals(rePassword)) {
            Integer update = userService.updatePassword(password, email);
            System.out.println("trạng thái cập nhật mật khẩu: " + update);
            return "redirect:/login";
        }
        modelMap.addAttribute("passwordNotMatch", "passwordNotMatch");
        return "enterpassword";
    }
}
