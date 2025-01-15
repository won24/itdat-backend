package com.itdat.back.controller.board;


import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.board.History;
import com.itdat.back.service.board.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/board/history")
@CrossOrigin
public class HistoryController {

    @Autowired
    private HistoryService historyService;


    @GetMapping("/{userEmail}")
    public ResponseEntity<List<History>> getHistoriesByUserEmail(@PathVariable String userEmail) {
        try{
            List<History> histories = historyService.getHistoriesByUserEmail(userEmail);
            return ResponseEntity.ok(histories);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping(value = "/save")
    public ResponseEntity<History> saveHistory(@RequestBody History history) {
        try {
            User user = historyService.findByUserEmail(history.getUserEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            History savedHistory = historyService.saveHistory(history);
            return ResponseEntity.ok(savedHistory);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<History> updateHistory(@PathVariable Integer id, @RequestBody History history) {
        try {

            History updated = historyService.updateHistory(id, history);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Integer id) {
        historyService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }

}
