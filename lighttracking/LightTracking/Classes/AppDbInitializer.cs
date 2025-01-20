using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace LightTracking.Classes
{
    public class AppDbInitializer: System.Data.Entity.DropCreateDatabaseIfModelChanges<AppDbContext>
    {

        protected override void Seed(AppDbContext context)
        {
            var user = new User
            {
                Name = "Administrator",
                Login = "admin",
                Password = "admin"
            };

            context.Users.Add(user);
            context.SaveChanges();

        }

    }
}