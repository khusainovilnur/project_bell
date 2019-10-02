package project.khusainov.organization.dao;

import org.springframework.stereotype.Repository;
import project.khusainov.organization.model.Organization;
import project.khusainov.organization.view.OrganizationByIdRespView;
import project.khusainov.organization.view.OrganizationListReqView;
import project.khusainov.organization.view.OrganizationListRespView;
import project.khusainov.organization.view.OrganizationUpdateReqView;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 */
@Repository
public class OrganizationDaoImpl implements OrganizationDao {

    private final EntityManager em;

    public OrganizationDaoImpl(EntityManager em) {
        this.em = em;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrganizationListRespView> getListByFilter(OrganizationListReqView organizationListReqView) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<OrganizationListRespView> criteriaQuery = criteriaBuilder.createQuery(OrganizationListRespView.class); // какого типа возвращаются
        Root<Organization> organization = criteriaQuery.from(Organization.class); // откуда берем

        criteriaQuery.select(criteriaBuilder.construct(OrganizationListRespView.class, organization.get("id"), organization.get("name"), organization.get("isActive"))); // выборка определенных полей

        // построение динамического запроса
        List<Predicate> allPredicates = new ArrayList<>(); // список фильтров
        allPredicates.add(criteriaBuilder.equal(organization.get("name"), organizationListReqView.name)); // обязательный фильтр по name

        if (organizationListReqView.inn != null) {
            allPredicates.add(criteriaBuilder.equal(organization.get("inn"), organizationListReqView.inn)); // необязательный фильтр по inn
        }
        if (organizationListReqView.isActive != null) {
            allPredicates.add(criteriaBuilder.equal(organization.get("isActive"), organizationListReqView.isActive)); // необязательный фильтр по isActive
        }

        criteriaQuery.where(
                criteriaBuilder.and(
                    allPredicates.toArray(new Predicate[0])
                )
        );

        TypedQuery<OrganizationListRespView> query = em.createQuery(criteriaQuery);
        List<OrganizationListRespView> organizationList = query.getResultList();

        return organizationList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationByIdRespView getOrganizationById(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<OrganizationByIdRespView> criteriaQuery = criteriaBuilder.createQuery(OrganizationByIdRespView.class); // какого типа возвращаются
        Root<Organization> organization = criteriaQuery.from(Organization.class); // откуда берем

        criteriaQuery.multiselect(
                organization.get("id"),
                organization.get("name"),
                organization.get("fullName"),
                organization.get("inn"),
                organization.get("kpp"),
                organization.get("address"),
                organization.get("phone"),
                organization.get("isActive")
        );

        criteriaQuery.where(
                criteriaBuilder.equal(organization.get("id"), id)
        );

        TypedQuery<OrganizationByIdRespView> query = em.createQuery(criteriaQuery);
        List<OrganizationByIdRespView> organizationByIdRespView = query.getResultList();

        return query.getResultList().size() > 0 ? organizationByIdRespView.get(0) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Organization organization) {
        em.persist(organization);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(OrganizationUpdateReqView organizationUpdateReqView) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaUpdate<Organization> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Organization.class); // что обновляем
        Root<Organization> organization = criteriaUpdate.from(Organization.class); // откуда берем

        criteriaUpdate
                .set(organization.get("id"),        organizationUpdateReqView.id)
                .set(organization.get("name"),      organizationUpdateReqView.name)
                .set(organization.get("fullName"),  organizationUpdateReqView.fullName)
                .set(organization.get("inn"),       organizationUpdateReqView.inn)
                .set(organization.get("kpp"),       organizationUpdateReqView.kpp)
                .set(organization.get("address"),   organizationUpdateReqView.address)
                .set(organization.get("phone"),     organizationUpdateReqView.phone)
                .set(organization.get("isActive"),  organizationUpdateReqView.isActive);

        criteriaUpdate
                .where(criteriaBuilder.equal(organization.get("id"), organizationUpdateReqView.id)
        );

        em.createQuery(criteriaUpdate).executeUpdate();
    }
}
