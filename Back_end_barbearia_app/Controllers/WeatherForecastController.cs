using Microsoft.AspNetCore.Mvc;
using Back_end_barbearia_app.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Back_end_barbearia_app.Controllers{
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
    public async Task<ActionResult<IEnumerable<Barbeiro>>> Get() =>
        await _context.Barbeiros.ToListAsync();

    [HttpPost]
    public async Task<ActionResult<Barbeiro>> Post(Barbeiro b)
    {
        _context.Barbeiros.Add(b);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(Get), new { id = b.Id }, b);
    }
}
}