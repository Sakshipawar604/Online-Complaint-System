
package com.project.smartportal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.project.smartportal.entity.User;
import com.project.smartportal.entity.Complaint;
import com.project.smartportal.repository.UserRepository;
import com.project.smartportal.repository.ComplaintRepository;
import com.project.smartportal.service.EmailService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService; // Inject EmailService

    @Autowired
    private ComplaintRepository complaintRepository;

    // LOGIN PAGE
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // REGISTER PAGE
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    // REGISTER USER
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String password,
                               @RequestParam String email) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userRepository.save(user);

        return "redirect:/login";
    }

    // SUBMIT COMPLAINT
    @PostMapping("/complaint")
    public String submitComplaint(@RequestParam String title,
                                  @RequestParam String description) {

        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setStatus("Pending");
        complaint.setDate(LocalDate.now());

        complaintRepository.save(complaint);

        // Send email notification to admin
        emailService.sendEmail(
                "sakshirajupawar2524@gmail.com", // Admin email
                "New Complaint Submitted",
                "A new complaint titled '" + title + "' has been submitted by a user."
        );

        return "redirect:/login";
    }

    // ADMIN VIEW ALL COMPLAINTS
    @GetMapping("/admin/complaints")
    public String viewComplaints(Model model) {
        List<Complaint> complaints = complaintRepository.findAll();
        model.addAttribute("complaints", complaints);
        return "view-complaints";
    }

    // MARK AS RESOLVED
    @GetMapping("/admin/resolve/{id}")
    public String resolveComplaint(@PathVariable Long id) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
            complaint.setStatus("Resolved");
            complaintRepository.save(complaint);
        }
        return "redirect:/admin/complaints";
    }

    // ADMIN DASHBOARD
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        List<Complaint> complaints = complaintRepository.findAll();
        long total = complaints.size();
        long pending = complaints.stream().filter(c -> c.getStatus().equals("Pending")).count();
        long resolved = complaints.stream().filter(c -> c.getStatus().equals("Resolved")).count();

        model.addAttribute("total", total);
        model.addAttribute("pending", pending);
        model.addAttribute("resolved", resolved);

        return "dashboard";
    }

    // SHOW ONLY PENDING
    @GetMapping("/admin/pending")
    public String viewPending(Model model) {
        List<Complaint> pendingComplaints = complaintRepository.findAll()
                .stream()
                .filter(c -> c.getStatus().equals("Pending"))
                .toList();

        model.addAttribute("complaints", pendingComplaints);
        return "view-complaints";
    }

    // SHOW ONLY RESOLVED
    @GetMapping("/admin/resolved")
    public String viewResolved(Model model) {
        List<Complaint> resolvedComplaints = complaintRepository.findAll()
                .stream()
                .filter(c -> c.getStatus().equals("Resolved"))
                .toList();

        model.addAttribute("complaints", resolvedComplaints);
        return "view-complaints";
    }

    // DELETE COMPLAINT
    @GetMapping("/admin/delete/{id}")
    public String deleteComplaint(@PathVariable Long id) {
        complaintRepository.deleteById(id);
        return "redirect:/admin/complaints";
    }

}