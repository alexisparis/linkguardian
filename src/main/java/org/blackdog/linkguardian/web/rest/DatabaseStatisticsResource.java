package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import java.util.List;
import org.blackdog.linkguardian.service.TableStatisticsService;
import org.blackdog.linkguardian.web.rest.vm.TableStatisticsVM;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/database")
public class DatabaseStatisticsResource {

    private final TableStatisticsService tableStatisticsService;

    public DatabaseStatisticsResource(TableStatisticsService tableStatisticsService) {
        this.tableStatisticsService = tableStatisticsService;
    }

    @GetMapping("/table/statistics")
    @Timed
    public List<TableStatisticsVM> getTableStatistics() {
        return this.tableStatisticsService.getTableStatistics();
    }
}
