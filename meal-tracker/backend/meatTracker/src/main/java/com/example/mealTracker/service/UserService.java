package com.example.mealTracker.service;


import com.example.mealTracker.domain.MealTrackerUser;
import com.example.mealTracker.dto.MeResponse;
import com.example.mealTracker.dto.MealTrackerUserResponse;
import com.example.mealTracker.dto.UpdateTargetsResponse;
import com.example.mealTracker.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public MealTrackerUserResponse getUser(String email) {
        MealTrackerUser user = userMapper.findByEmail(email);

        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return new MealTrackerUserResponse(user);
    }

    public MeResponse updateTargets(String userId, UpdateTargetsResponse vo) {
        int cal = vo.targetCalories() == null ? 0 : vo.targetCalories();
        int prot = vo.targetProtein() == null ? 0 : vo.targetProtein();

        int updated = userMapper.updateTargets(userId, cal, prot);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        MealTrackerUser user = userMapper.findByEmail(userId);

        return new MeResponse(user.getEmail(), user.getTargetCalories(), user.getTargetProtein());
    }
}
