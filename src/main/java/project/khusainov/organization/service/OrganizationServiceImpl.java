package project.khusainov.organization.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.khusainov.exception.NotFoundException;
import project.khusainov.organization.dao.OrganizationDao;
import project.khusainov.organization.model.Organization;
import project.khusainov.organization.view.OrganizationByIdRespView;
import project.khusainov.organization.view.OrganizationListReqView;
import project.khusainov.organization.view.OrganizationListRespView;
import project.khusainov.organization.view.OrganizationSaveReqView;
import project.khusainov.organization.view.OrganizationUpdateReqView;

import java.util.List;

/**
 * {@inheritDoc}
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private OrganizationDao dao;

    @Autowired
    public OrganizationServiceImpl(OrganizationDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrganizationListRespView> getOrganizationByFilter(OrganizationListReqView organizationListReqView) {
        return dao.getListByFilter(organizationListReqView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public OrganizationByIdRespView getOrganizationById(Long id) {
        if (dao.getOrganizationById(id) == null) {
            throw new NotFoundException("Организация с таким id не найдена");
        }
        else {
            return dao.getOrganizationById(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void saveOrganization(OrganizationSaveReqView organizationSaveReqView) {
        Organization organization = new Organization
                (
                        organizationSaveReqView.name,
                        organizationSaveReqView.fullName,
                        organizationSaveReqView.inn,
                        organizationSaveReqView.kpp,
                        organizationSaveReqView.address,
                        organizationSaveReqView.phone,
                        organizationSaveReqView.isActive
                );

        dao.save(organization);
        LOGGER.debug("Новая организация добавлена");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateOrganization(OrganizationUpdateReqView organizationUpdateReqView) {
        dao.update(organizationUpdateReqView);
        LOGGER.debug("Организация с id={} изменена", organizationUpdateReqView.id);
    }
}
