package ubb.scs.map.domain.validators;

public interface Validator<T> {
    /**
     *
     * @param entity refers the entity that will be validated
     */
    void validate(T entity);
}