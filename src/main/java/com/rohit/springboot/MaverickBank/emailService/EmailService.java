
package com.rohit.springboot.MaverickBank.emailService;

import com.rohit.springboot.MaverickBank.emailService.EmailDetails;


public interface EmailService {

    String sendSimpleMail(EmailDetails details);

}
