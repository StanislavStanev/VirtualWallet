package com.team14.virtualwallet.utils;

import com.team14.virtualwallet.models.dtos.SearchByDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerHelper {

    public static String fillQueryString(SearchByDto searchByDto) {
        List<String> queryStringParameters = new ArrayList<>();

        if (!searchByDto.getFrom().equals("")) {
            queryStringParameters.add(String.format("from=%s", searchByDto.getFrom()));
        }

        if (!searchByDto.getTo().equals("")) {
            queryStringParameters.add(String.format("to=%s", searchByDto.getTo()));
        }

        if (searchByDto.getSender() != null) {
            queryStringParameters.add(String.format("sender=%s", searchByDto.getSender()));
        }

        if (searchByDto.getRecipient() != null) {
            queryStringParameters.add(String.format("recipient=%s", searchByDto.getRecipient()));
        }

        if (searchByDto.getType() != null) {
            queryStringParameters.add(String.format("type=%s", searchByDto.getType()));
        }

        if (!searchByDto.getSort().equals("")) {
            queryStringParameters.add(String.format("sort=%s", searchByDto.getSort()));
        }

        if (queryStringParameters.size() > 0) {
            String result = "?" + StringUtils.join(queryStringParameters, "&");
            return result;
        }

        return "";
    }

    public static boolean updateTypeIsWrong(String updateType) {
        return !updateType.equals("password") && !updateType.equals("personalInfo") && !updateType.equals("profilePicture") && !updateType.equals("");
    }

    public static String generateFieldErrorsMessage(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(" <---> "));
    }

    public static void setErrorDetails(RedirectAttributes attr, String errorText) {
        attr.addFlashAttribute("error", "error");
        attr.addFlashAttribute("errorText", errorText);
    }

    public static void setSuccessDetails(RedirectAttributes attr, String successText) {
        attr.addFlashAttribute("success", "success");
        attr.addFlashAttribute("successText", successText);
    }
}
