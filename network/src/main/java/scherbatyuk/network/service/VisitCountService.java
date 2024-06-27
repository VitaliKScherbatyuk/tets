/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.VisitCountRepository;
import scherbatyuk.network.domain.VisitCount;

/**
 * Controller for calculating the opening of the site
 */
@Service
public class VisitCountService {

    @Autowired
    private VisitCountRepository visitCountRepository;

    /**
     * Retrieves the visit count for a specific page.
     *
     * @param pageName the name of the page
     * @return the visit count for the specified page
     */
    public int getVisitCount(String pageName) {

        return visitCountRepository.findByPageName(pageName)
                .map(VisitCount::getVisitCount)
                .orElse(0);
    }

    /**
     * Increments the visit count for a specific page.
     *
     * @param pageName the name of the page
     */
    public void incrementVisitCount(String pageName) {

        VisitCount visitCount = visitCountRepository.findByPageName(pageName)
                .orElse(new VisitCount());
        visitCount.setPageName(pageName);
        visitCount.setVisitCount(visitCount.getVisitCount() + 1);

        visitCountRepository.save(visitCount);
    }

}
