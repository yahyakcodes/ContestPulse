package com.contestpulse.service;

import com.contestpulse.model.Contest;
import com.contestpulse.provider.ContestProvider;
import com.contestpulse.repository.ContestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates one contest-sync run: ask a ContestProvider what's out there,
 * then save whatever isn't already in the database.
 *
 * @Service marks this as a Spring-managed bean (business-logic layer,
 * as opposed to @Component/@Repository/@Controller -- functionally identical,
 * the different name just documents intent).
 *
 * No @Transactional here on purpose: ContestProvider.fetchContests() makes an
 * external HTTP call, and a @Transactional method would hold a database
 * connection open for the whole method body -- including that network call.
 * Each contestRepository.save() below is already transactional on its own
 * (Spring Data JPA wraps every repository method in its own transaction by
 * default), so partial progress is kept even if one save fails partway
 * through a large batch, which is the behavior we want for a sync job.
 */
@Slf4j
@Service
public class ContestSyncService {

    private final ContestProvider contestProvider;
    private final ContestRepository contestRepository;

    // Only one ContestProvider bean exists today (CodeforcesContestProvider),
    // so Spring can inject the interface directly with no ambiguity. Once a
    // second platform is added, this constructor will need to change to
    // accept List<ContestProvider> and loop over all of them -- deliberately
    // not building that now since there's nothing to loop over yet.
    public ContestSyncService(ContestProvider contestProvider, ContestRepository contestRepository) {
        this.contestProvider = contestProvider;
        this.contestRepository = contestRepository;
    }

    public ContestSyncResult syncContests() {
        List<Contest> fetched = contestProvider.fetchContests();

        int newlySaved = 0;
        int alreadyExisted = 0;

        for (Contest contest : fetched) {
            boolean exists = contestRepository
                    .findByPlatformAndExternalId(contest.getPlatform(), contest.getExternalId())
                    .isPresent();

            if (exists) {
                alreadyExisted++;
            } else {
                contestRepository.save(contest);
                newlySaved++;
            }
        }

        log.info("Contest sync complete: fetched={}, newlySaved={}, alreadyExisted={}",
                fetched.size(), newlySaved, alreadyExisted);

        return new ContestSyncResult(fetched.size(), newlySaved, alreadyExisted);
    }
}
