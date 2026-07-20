package com.honda.managerlmsmini.service.impl;

import com.honda.managerlmsmini.entity.Course;
import com.honda.managerlmsmini.entity.Student;
import com.honda.managerlmsmini.exception.AppException;
import com.honda.managerlmsmini.exception.ErrorCode;
import com.honda.managerlmsmini.service.ExcelService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Override
    public byte[] students(List<Student> students) {
        List<String> headers = List.of("ID", "Ho ten", "Email", "Dien thoai");
        List<List<Object>> rows = students.stream()
                .map(student -> List.<Object>of(
                        student.getId(),
                        student.getName(),
                        student.getEmail(),
                        Objects.toString(student.getPhone(), "")))
                .toList();
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Hoc vien");
            var headerRow = sheet.createRow(0);
            for (int column = 0; column < headers.size(); column++) {
                headerRow.createCell(column).setCellValue(headers.get(column));
            }
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                var row = sheet.createRow(rowIndex + 1);
                List<Object> values = rows.get(rowIndex);
                for (int column = 0; column < values.size(); column++) {
                    row.createCell(column).setCellValue(String.valueOf(values.get(column)));
                }
            }
            for (int column = 0; column < headers.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new AppException(ErrorCode.EXCEL_EXPORT_ERROR, exception);
        }
    }

    @Override
    public byte[] courses(List<Course> courses) {
        List<String> headers = List.of("ID", "Ma", "Ten khoa hoc", "Thoi luong");
        List<List<Object>> rows = courses.stream()
                .map(course ->
                        List.<Object>of(course.getId(), course.getCode(), course.getName(), course.getDuration()))
                .toList();
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Khoa hoc");
            var headerRow = sheet.createRow(0);
            for (int column = 0; column < headers.size(); column++) {
                headerRow.createCell(column).setCellValue(headers.get(column));
            }

            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                var row = sheet.createRow(rowIndex + 1);
                List<Object> values = rows.get(rowIndex);
                for (int column = 0; column < values.size(); column++) {
                    row.createCell(column).setCellValue(String.valueOf(values.get(column)));
                }
            }

            for (int column = 0; column < headers.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new AppException(ErrorCode.EXCEL_EXPORT_ERROR, exception);
        }
    }
}
