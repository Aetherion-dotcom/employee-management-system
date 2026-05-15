package com.workforcehub.service;

import com.workforcehub.entity.Employee;
import com.workforcehub.enums.EmployeeStatus;
import com.workforcehub.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final EmployeeRepository employeeRepository;

    public byte[] exportEmployeesToExcel() {
        List<Employee> employees = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employees");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Employee ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Department");
            headerRow.createCell(4).setCellValue("Designation");
            headerRow.createCell(5).setCellValue("Status");
            headerRow.createCell(6).setCellValue("Joining Date");

            int rowIdx = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(emp.getEmployeeId());
                row.createCell(1).setCellValue(emp.getFullName());
                row.createCell(2).setCellValue(emp.getEmail());
                row.createCell(3).setCellValue(emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A");
                row.createCell(4).setCellValue(emp.getDesignation());
                row.createCell(5).setCellValue(emp.getStatus().name());
                row.createCell(6).setCellValue(emp.getJoiningDate() != null ? emp.getJoiningDate().toString() : "N/A");
            }

            // Auto-size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.info("Excel report generated with {} employees.", employees.size());
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate Excel report", e);
            throw new RuntimeException("Excel generation failed");
        }
    }

    public byte[] exportEmployeesToPdf() {
        List<Employee> employees = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Employee Directory Report"));
            document.add(new Paragraph("Total Employees: " + employees.size()));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph(" "));

            for (Employee emp : employees) {
                String deptName = emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A";
                document.add(new Paragraph(
                    "ID: " + emp.getEmployeeId() +
                    " | Name: " + emp.getFullName() +
                    " | Dept: " + deptName +
                    " | Designation: " + emp.getDesignation()
                ));
            }

            document.close();
            log.info("PDF report generated with {} employees.", employees.size());
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF report", e);
            throw new RuntimeException("PDF generation failed");
        }
    }
}
