package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;

import com.penguin.linknote.entity.AccessPolicies;

public interface AccessPoliciesRepository {
    List<AccessPolicies> index(Integer limit);

    Optional<AccessPolicies> get(Integer id);

    AccessPolicies create(AccessPolicies policy);

    AccessPolicies update(AccessPolicies policy);

    void delete(Integer id);
}
