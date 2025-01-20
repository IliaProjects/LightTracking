using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel.DataAnnotations;

namespace LightTracking.Models
{
    public class GPS
    {
        [Key]
        public int Id { get; set; }

        public int UserId { get; set; }

        [Required]
        public DateTime Date { get; set; }

        [Required]
        public String Latitude { get; set; }

        [Required]
        public String Longitude { get; set; }

        public virtual User User { get; set; }

    }
}
