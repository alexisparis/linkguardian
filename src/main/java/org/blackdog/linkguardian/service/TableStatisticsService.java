package org.blackdog.linkguardian.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.ManagedType;
import org.blackdog.linkguardian.web.rest.vm.TableStatisticsVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TableStatisticsService {

    private final Logger log = LoggerFactory.getLogger(TableStatisticsService.class);

    private final EntityManager entityManager;

    public TableStatisticsService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<TableStatisticsVM> getTableStatistics() {

        List<TableStatisticsVM> results = new ArrayList<>();

        Set<ManagedType<?>> managedTypes = this.entityManager.getMetamodel().getManagedTypes();

        log.info("analyzing jpa metadata : ");
        managedTypes
            .stream()
            .forEach(managedType -> {
                Class<?> javaType = managedType.getJavaType();

                log.info("javaType : " + javaType);

                Table tableAnnotation = javaType.getAnnotation(Table.class);

                if (tableAnnotation != null) {
                    TableStatisticsVM stat = new TableStatisticsVM();

                    Field pkField = null;
                    for(int i = 0; i < javaType.getDeclaredFields().length && pkField == null; i++) {
                        Field currentField = javaType.getDeclaredFields()[i];
                        if(currentField.getAnnotation(Id.class) != null) {
                            pkField = currentField;
                        }
                    }

                    stat.setTablename(tableAnnotation.name());
                    stat.setItemCount(((Number) this.entityManager.createQuery("SELECT COUNT(e) FROM " + javaType.getSimpleName() + " e").getSingleResult()).longValue());

                    if (pkField != null && Number.class.isAssignableFrom(pkField.getType())) {
                        Object max =
                            this.entityManager.createQuery("SELECT MAX(e." + pkField.getName() + ") FROM " + javaType.getSimpleName() + " e").getSingleResult();
                        if (max instanceof Number) {
                            stat.setGreatestPkValue(((Number)max).longValue());
                        }

                        SequenceGenerator sequenceGenAnnotation = pkField.getAnnotation(SequenceGenerator.class);
                        if (sequenceGenAnnotation != null) {
                            stat.setPkSequenceName(sequenceGenAnnotation.sequenceName());

                            try {
                                stat.setPkSequenceNextValue(
                                    ((Number) this.entityManager.createNativeQuery("SELECT last_value FROM " + sequenceGenAnnotation.sequenceName()).getSingleResult()).longValue()
                                );
                            } catch (javax.persistence.PersistenceException e) {
                                log.error("could not get current value of sequence " + sequenceGenAnnotation.sequenceName(), e);
                            }
                        }
                    }
                    log.info("   " + stat);

                    results.add(stat);
                }
            });

        results.sort(Comparator.comparing(TableStatisticsVM::getTablename));

        return results;
    }

}
