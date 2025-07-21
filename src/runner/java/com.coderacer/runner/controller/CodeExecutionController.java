package com.coderacer.runner.controller;
//
//import com.coderacer.runner.model.CodeSubmission;
//import com.coderacer.runner.model.ExecutionResult;
//import com.coderacer.runner.service.CodeExecutionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/code")
//public class CodeExecutionController {
//    @Autowired
//    private CodeExecutionService svc;
//
//    @PostMapping("/submit")
//    public ResponseEntity<ExecutionResult> submit(@RequestBody CodeSubmission sub) {
////        try {
////            return ResponseEntity.ok(svc.executeCode(sub));
////        } catch (Exception e) {
////            return ResponseEntity.status(500)
//////                    .body(.error("Code execution resul status: " + ExecutionResult.getStatus());
////        }
//    }
//}