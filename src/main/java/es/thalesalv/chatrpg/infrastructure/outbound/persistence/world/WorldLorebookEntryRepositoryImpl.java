package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.core.application.usecase.world.request.SearchWorldLorebookEntries;
import es.thalesalv.chatrpg.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.mapper.WorldLorebookPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorldLorebookEntryRepositoryImpl implements WorldLorebookEntryRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;
    private static final String DEFAULT_SORT_BY_FIELD = "name";

    private final WorldLorebookEntryJpaRepository jpaRepository;
    private final WorldLorebookPersistenceMapper mapper;

    @Override
    public WorldLorebookEntry save(WorldLorebookEntry world) {

        WorldLorebookEntryEntity entity = mapper.mapToEntity(world);

        return mapper.mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<WorldLorebookEntry> findById(String lorebookEntryId) {

        return jpaRepository.findById(lorebookEntryId)
                .map(mapper::mapFromEntity);
    }

    @Override
    public List<WorldLorebookEntry> findAllEntriesByRegex(String valueToSearch) {

        return jpaRepository.findAllByNameRegex(valueToSearch)
                .stream()
                .map(mapper::mapFromEntity)
                .toList();
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public SearchWorldLorebookEntriesResult searchWorldLorebookEntriesByWorldId(SearchWorldLorebookEntries query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<WorldLorebookEntryEntity> filters = searchLorebookSpecificationFrom(query);
        Page<WorldLorebookEntryEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    private Specification<WorldLorebookEntryEntity> searchLorebookSpecificationFrom(SearchWorldLorebookEntries query) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("worldId"), query.getWorldId()));

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.and(cb.like(cb.upper(root.get("name")),
                        "%" + query.getName().toUpperCase() + "%")));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
