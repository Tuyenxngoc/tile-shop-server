package com.example.tileshop.util;

import com.example.tileshop.dto.common.DataMailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendMailUtil {
    JavaMailSender mailSender;

    TemplateEngine templateEngine;

    /**
     * Gửi mail với file html
     *
     * @param mail     Thông tin của mail cần gửi
     * @param template Tên file html trong folder resources/template
     *                 Example: Index.html
     */
    public void sendEmailWithHTML(DataMailDTO mail, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        Context context = new Context();
        context.setVariables(mail.getProperties());
        String htmlMsg = templateEngine.process(template, context);
        helper.setText(htmlMsg, true);
        mailSender.send(message);
    }

    /**
     * Gửi mail với tệp đính kèm
     *
     * @param mail  Thông tin của mail cần gửi
     * @param files File cần gửi
     */
    public void sendMailWithAttachment(DataMailDTO mail, MultipartFile[] files) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getContent());
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }
        mailSender.send(message);
    }

    /**
     * Gửi mail với nội dung HTML và file đính kèm
     *
     * @param mail            Thông tin mail cần gửi
     * @param template        Tên file HTML template trong resources/templates
     * @param attachmentBytes File đính kèm dạng byte[]
     * @param attachmentName  Tên file đính kèm
     */
    public void sendMailWithAttachmentAndHtml(DataMailDTO mail, String template, byte[] attachmentBytes, String attachmentName)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());

        // Xử lý nội dung HTML
        Context context = new Context(Locale.of("vi", "VN"));
        context.setVariables(mail.getProperties());
        String htmlMsg = templateEngine.process(template, context);
        helper.setText(htmlMsg, true);

        // Đính kèm file PDF từ byte array
        if (attachmentBytes != null && attachmentName != null) {
            helper.addAttachment(attachmentName, new ByteArrayDataSource(attachmentBytes, "application/pdf"));
        }

        mailSender.send(message);
    }
}
