using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Back_end_barbearia_app.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class BarbeiroController : ControllerBase
    {
        private readonly BarberTechContext _context;

        public BarbeiroController(BarberTechContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Barbeiro>>> GetAll() =>
            await _context.Barbeiros.ToListAsync();

        [HttpGet("{id}")]
        public async Task<ActionResult<Barbeiro>> GetById(int id)
        {
            var entity = await _context.Barbeiros.FindAsync(id);
            return entity == null ? NotFound() : entity;
        }

        [HttpPost]
        public async Task<ActionResult<Barbeiro>> Create(Barbeiro b)
        {
            _context.Barbeiros.Add(b);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetById), new { id = b.Id }, b);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, Barbeiro b)
        {
            if (id != b.Id) return BadRequest();
            _context.Entry(b).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var entity = await _context.Barbeiros.FindAsync(id);
            if (entity == null) return NotFound();
            _context.Barbeiros.Remove(entity);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
