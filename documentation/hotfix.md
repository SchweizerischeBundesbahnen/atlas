# Hotfix: Build and Deployment

If you ever have to deploy a hotfix to atlas follow this easy guide:

## Select the right base to perform the changes on
1. Identify the version where you would like to branch off (normally production version, here we will use 2.344.0 as an example)
2. Check it out `git checkout 2.344.0`
3. Create a branch from there `git branch -b feature/my-special-hotfix` (name might be to your needs)
4. Perform your hotfix changes there

## Perform the hotfix build
5. Once the continuous integration is complete - go to tekton and locate your hotfix branch in the `Branches` table
6. Click on the `Create new tag` icon
7. Choose the appropriate version - 2.344.1 would be nice for our hotfix example
8. Set the Developer Version to `None`
9. Execute the build. Tekton will perform a project version upgrade to the chosen version and tag the commit afterwards.
10. You can now deploy 2.344.1 to the stages you want

## Merge changes to master
11. Don't forget: Revert the tekton version upgrade from the branch via `git revert` and make sure to merge it to the master 
    branch!