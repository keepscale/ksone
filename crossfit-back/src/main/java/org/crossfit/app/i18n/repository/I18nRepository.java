package org.crossfit.app.i18n.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.crossfit.app.i18n.domain.I18nGroupNameKey;
import org.crossfit.app.i18n.domain.I18nKey;
import org.crossfit.app.i18n.domain.I18nValue;
import org.crossfit.app.i18n.domain.enumeration.LangKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the I18nValue entity.
 */
public interface I18nRepository extends JpaRepository<I18nValue,Long> {

    @Query("select val from I18nValue val "
    		+ "left join fetch val.i18nKey key "
    		+ "where key.groupName = :#{#groupName.name} and val.langKey = :langKey")
	List<I18nValue> findAllByGroupName(@Param("groupName") I18nGroupNameKey groupName, @Param("langKey") LangKey langKey);

    @Query("select val from I18nValue val "
    		+ "left join fetch val.i18nKey key "
    		+ "where key.groupName = :#{#groupName.name}")
    List<I18nValue> findAllByGroupName(@Param("groupName") I18nGroupNameKey groupName);

    @Query("select key from I18nKey key "
    		+ "where key.groupName = :#{#groupName.name} and  key.name = :name")
	I18nKey findI18nKey(@Param("name") String name, @Param("groupName") I18nGroupNameKey groupName);

    @Transactional
    @Modifying
    @Query("delete from I18nValue val where exists ( select 1 from I18nKey key where  val.i18nKey = key and key.groupName  = :#{#groupName.name})")
	void deleteValuesByGroupName(@Param("groupName") I18nGroupNameKey groupName);
    
    @Transactional
    @Modifying
    @Query("delete from I18nKey key  where key.groupName  = :#{#groupName.name}")
	void deleteKeyByGroupName(@Param("groupName") I18nGroupNameKey groupName);

    @Transactional
    @Modifying
    @Query("delete from I18nValue val where val.i18nKey = :key")
	void deleteValuesOf(@Param("key") I18nKey key);

    @Transactional
    @Modifying
    @Query("delete from I18nKey key where key = :key")
	void deleteKeyOf(@Param("key") I18nKey key);
    

}
