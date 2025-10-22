using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Back_end_barbearia_app.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ProdutoController : ControllerBase
    {
        private readonly BarberTechContext _context;

        public ProdutoController(BarberTechContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Produto>>> GetAll() =>
            await _context.Produtos.ToListAsync();

        [HttpGet("{id}")]
        public async Task<ActionResult<Produto>> GetById(int id)
        {
            var entity = await _context.Produtos.FindAsync(id);
            return entity == null ? NotFound() : entity;
        }

        [HttpPost]
        public async Task<ActionResult<Produto>> Create(Produto p)
        {
            _context.Produtos.Add(p);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetById), new { id = p.Id }, p);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, Produto p)
        {
            if (id != p.Id) return BadRequest();
            _context.Entry(p).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var entity = await _context.Produtos.FindAsync(id);
            if (entity == null) return NotFound();
            _context.Produtos.Remove(entity);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
