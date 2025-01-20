using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Web;

namespace LightTracking.Classes
{
    public class AppDbContext : DbContext
    {
        public AppDbContext() : base("AppDbContext")
        {

        }

        public DbSet<User> Users { get; set; }
        public DbSet<UserSession> UserSessions { get; set; }
        public DbSet<Call> Calls { get; set; }
        public DbSet<Sms> SmsMessages { get; set; }
        public DbSet<GPS> GPSTracking { get; set; }

    }
}