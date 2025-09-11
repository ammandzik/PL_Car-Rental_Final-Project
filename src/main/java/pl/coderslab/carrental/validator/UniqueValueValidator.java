package pl.coderslab.carrental.validator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.transaction.annotation.Transactional;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private String fieldName;
    private Class<?> entityClass;

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.entityClass = constraintAnnotation.entity();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        var query = String.format("""
                SELECT COUNT(e) FROM %s e
                WHERE e.%s = :value
                """, entityClass.getSimpleName(), fieldName);

        var count = entityManager.createQuery(query, Long.class)
                .setParameter("value", value)
                .getSingleResult();

        return count == 0;
    }
}
