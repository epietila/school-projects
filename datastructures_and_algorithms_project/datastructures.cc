// Datastructures.cc

#include "datastructures.hh"

#include <random>

#include <cmath>

std::minstd_rand rand_engine; // Reasonably quick pseudo-random generator

template <typename Type>
Type random_in_range(Type start, Type end)
{
    auto range = end-start;
    ++range;

    auto num = std::uniform_int_distribution<unsigned long int>(0, range-1)(rand_engine);

    return static_cast<Type>(start+num);
}

// Modify the code below to implement the functionality of the class.
// Also remove comments from the parameter names when you implement
// an operation (Commenting out parameter name prevents compiler from
// warning about unused parameters on operations you haven't yet implemented.)

Datastructures::Datastructures()
{
    // Replace this comment with your implementation
}

Datastructures::~Datastructures()
{
    // Replace this comment with your implementation
}

int Datastructures::place_count()
{
    // Return the amount of places saved
    return place_data.size();
}

void Datastructures::clear_all()
{
    // Clear all data structures
    place_data.clear();
    area_data.clear();
    place_vec.clear();
}

std::vector<PlaceID> Datastructures::all_places()
{
   // Vector to save place IDs
    std::vector<PlaceID> place_vec = {};

    // Go through the place_data map and add the keys to the place_vec vector
    for (auto iter = place_data.begin();
         iter != place_data.end(); ++iter)
    {
        place_vec.push_back(iter->first);
    }
    return place_vec;
}

bool Datastructures::add_place(PlaceID id, const Name& name, PlaceType type,
                               Coord xy)
{
    // If a place with id doesn't already exist, create a new place and insert
    // it into both data structures storing place data
    Place new_place = {id, name, xy, type};
    std::shared_ptr<Place> p( std::make_shared<Place>(new_place));
    if (place_data.insert({id, p}).second == true)
    {
        place_vec.push_back(&(*p));
        return true;
    }
    return false;
}

std::pair<Name, PlaceType> Datastructures::get_place_name_type(PlaceID id)
{
    auto iter = place_data.find(id);
    // If the place with id exists, return its name and type, otherwise
    // return NO_NAME and NO_TYPE
    if (iter != place_data.end())
    {
        return {iter->second->name, iter->second->type};
    }
    else
    {
        return {NO_NAME, PlaceType::NO_TYPE};
    }
}

Coord Datastructures::get_place_coord(PlaceID id)
{
    auto iter = place_data.find(id);

    // If the place exist, return its coordinates or else return NO_COORD
    if (iter != place_data.end())
    {
        return iter->second->coordinates;
    }
    else
    {
        return NO_COORD;
    }
}

bool Datastructures::add_area(AreaID id, const Name &name, std::vector<Coord>
                              coords)
{
    Area new_area = {id, name, coords};
    std::shared_ptr<Area> p(std::make_shared<Area>(new_area));

    // If an area with id doesn't already exist, create new one and save it
    // to area_data and return true. Otherwise return false.
    if (area_data.insert({id, p}).second == true)
    {
        return true;
    }
    return false;
}

Name Datastructures::get_area_name(AreaID id)
{
    auto iter = area_data.find(id);
    // If the area exist, return its name or else return NO_NAME
    if (iter != area_data.end())
    {
        return {iter->second->name};
    }
    else
    {
        return {NO_NAME};
    }
}

std::vector<Coord> Datastructures::get_area_coords(AreaID id)
{
    auto iter = area_data.find(id);
    // If the area, return its coordinates or else return NO_COORD
    if (iter != area_data.end())
    {
        return iter->second->coordinates;
    }
    else
    {
        return {NO_COORD};
    }
}

void Datastructures::creation_finished()
{
    adding_finished = true;

    // Change status of coord_order to prevent errors
    coord_order = Not_sorted;
}


std::vector<PlaceID> Datastructures::places_alphabetically()
{
    // Make temporary containers for storing the data
    std::vector<PlaceID> v = {};
    std::multimap<Name, PlaceID> m = {};

    // Add all places in a multimap for an automatic sorting
    for (auto iter = place_data.begin(); iter != place_data.end(); ++iter)
    {
        m.insert({iter->second->name, iter->first});
    }

    // Transfer the ids from the multimap into vector
    for(std::multimap<Name, PlaceID>::iterator it = m.begin(); it != m.end(); ++it)
    {
        v.push_back(it->second);
    }
    return v;
}

std::vector<PlaceID> Datastructures::places_coord_order()
{
    std::vector<PlaceID> v = {};

    // Sort place_vec if it might be unordered
    if (!adding_finished && coord_order != From_origin)
    {
        if (place_vec.size() == 0)
        {
            return v;
        }
        Coord origin = {0, 0};

        // Sort the vector according to the distance from origin
        unsigned long start = 0;
        unsigned long end = place_vec.size()-1;
        merge_sort(place_vec, start, end, origin);
        coord_order = From_origin;
    }

    // Add IDs to the final vector
    for (auto iter = place_vec.begin(); iter != place_vec.end(); ++iter)
    {
        v.push_back((*iter)->ID);
    }

    return v;
}

std::vector<PlaceID> Datastructures::find_places_name(Name const& name)
{
    std::vector<PlaceID> v = {};
    // Go through the place data and get all places with desired name into vector v
    for (auto iter = place_data.begin(); iter != place_data.end(); ++iter)
    {
        if (iter->second->name == name)
        {
            v.push_back(iter->first);
        }
    }
    return v;
}

std::vector<PlaceID> Datastructures::find_places_type(PlaceType type)
{
    std::vector<PlaceID> v = {};
    // Go through the place data and get all places with desired type into vector v
    for (auto iter = place_data.begin(); iter != place_data.end(); ++iter)
    {
        if (iter->second->type == type)
        {
            v.push_back(iter->first);
        }
    }
    return v;
}

bool Datastructures::change_place_name(PlaceID id, const Name& newname)
{
    // Get an iterator to the place, change its name if it exists and return
    // true. Otherwise return false.
    auto iter = place_data.find(id);
    if (iter != place_data.end())
    {
        PlaceType type = iter->second->type;
        Coord coordinates = iter->second->coordinates;

        place_data.erase(iter);
        add_place(id, newname, type, coordinates);

        return true;
    }
    return false;
}

bool Datastructures::change_place_coord(PlaceID id, Coord newcoord)
{
    // Get an iterator to the place and change its coordinates if it exists
    auto iter = place_data.find(id);
    if (iter != place_data.end())
    {
        // Change the coordinates and order status of coordinates and return true
        iter->second->coordinates = newcoord;
        coord_order = Not_sorted;

        return true;
    }
    return false;
}

std::vector<AreaID> Datastructures::all_areas()
{
    std::vector<AreaID> v = {};
    // Copy all the AreaIDs to the vector to be returned
    for (auto iter = area_data.begin(); iter != area_data.end() ; ++iter )
    {
        v.push_back(iter->first);
    }
    return v;
}

bool Datastructures::add_subarea_to_area(AreaID id, AreaID parentid)
{
    auto sub_area = area_data[id];
    auto area = area_data[parentid];

    // If invalid areas were given, return false. Otherwise add parent and
    // child information to the areas and return true.
    if (!(sub_area && area) or (sub_area->parent != nullptr))
    {
        return false;
    }
    else
    {
        sub_area->parent = &(*area);
        area->children.push_back(&(*sub_area));
        return true;
    }
}

std::vector<AreaID> Datastructures::subarea_in_areas(AreaID id)
{
    std::vector<AreaID> v = {}; 
    auto area = area_data[id];
    // If the area doesn't have a parent, return NO_AREA. Otherwise find
    // parents with recursion and return a vector containing them.
    if (area->parent == nullptr)
    {
        return {NO_AREA};
    }
    else
    {
        v.push_back(id);
        parent_area(v, area->parent->ID);
        return  v;
    }

}

std::vector<PlaceID> Datastructures::places_closest_to(Coord xy, PlaceType type)
{
    std::vector<PlaceID> v = {};

    // Sort place_vec if needed
    if (!adding_finished && coord_order != Closest_to)
    {
        unsigned long start = 0;
        unsigned long end = place_vec.size()-1;
        merge_sort(place_vec, start, end, xy);
        coord_order = Closest_to;
    }

    // Add 3 first places with correct type to the final vector
    int places = 0;
    for (auto iter = place_vec.begin(); iter != place_vec.end(); ++ iter)
    {
        if (((*iter)->type == type) or type == PlaceType::NO_TYPE)
        {
            v.push_back((*iter)->ID);
            if (++places == 3)
            {
                break;
            }
        }
    }

    return v;
}

bool Datastructures::remove_place(PlaceID id)
{
    // Find iterator to the place to be removed and remove it if possible.
    auto iter = place_data.find(id);
    if (iter == place_data.end())
    {
        return false;
    }
    else
    {
        // Find and erase from place_vec
        for ( auto iter2 = place_vec.begin(); iter2 != place_vec.end(); ++iter2)
        {
            if((*iter2)->ID == id)
            {
                place_vec.erase(iter2);
                break;
            }
        }
        // Erase from place_data
        place_data.erase(iter);

        return true;
    }
}

std::vector<AreaID> Datastructures::all_subareas_in_area(AreaID id)
{
    std::vector<AreaID> v = {};
    // If the area doesn't have any children, return NO_AREA. Otherwise find
    // them with recursion.
    if (area_data.at(id)->children.size() == 0)
    {
        v.push_back(NO_AREA);
    }
    else
    {
        subareas(v, id);
    }
    return v;
}

AreaID Datastructures::common_area_of_subareas(AreaID id1, AreaID id2)
{
    return NO_AREA;
}

void Datastructures::parent_area(std::vector<AreaID> &v, AreaID& id)
{
    auto area = area_data[id];
    // End recursion if the area doesn't have a parent
    if (area->parent == nullptr)
    {
        v.push_back(id);
        return;
    }
    else
    {
        v.push_back(id);
        // Find parents
        parent_area(v, area->parent->ID);
    }
}

void Datastructures::subareas(std::vector<AreaID> &v, AreaID& id)
{
    auto area = area_data.at(id);

    // End recursion if no more children are found
    if (area->children.size() == 0)
    {
        v.push_back(id);
        return;
    }
    else
    {
        v.push_back(id);

        // Find grandchild areas
        for (unsigned long i = 0; i < area->children.size(); ++i )
        {
            subareas(v, area->children.at(i)->ID);
        }       
    }
}

double Datastructures::coord_distance(Coord &a, Coord &b)
{
    // Return distance of two coordinates
    return sqrt(pow((a.x - b.x), 2.0) + pow((a.y - b.y), 2.0));
}

void Datastructures::merge(std::vector<Datastructures::Place* > &v,
                           unsigned long &left, unsigned long &mid,
                           unsigned long &right, Coord &other_coord)
{
    // Create a copy of the vector v
    std::vector<Datastructures::Place*> v2 = v;

    auto i = left;
    auto j = left;
    auto k = mid + 1;
    while (j <= mid && k <= right)
    {
        // calculate distances for j and k
        double jd = coord_distance(v2.at(j)->coordinates, other_coord);
        double kd = coord_distance(v2.at(k)->coordinates, other_coord);
        if (jd < kd)
        {
            // Insert the smaller value into the result vector
            v.at(i) = v2.at(j);
            // move the strating point
            ++j;
        }
        else if (jd == kd && v2.at(j)->coordinates.y <= v2.at(k)->coordinates.y)
        {
            // Insert the smaller value into the result vector
            v.at(i) = v2.at(j);
            // Move the strating point
            ++j;
        }
        else
        {
            // Insert the smaller value into the result vector
            v.at(i) = v2.at(k);
            // Move the strating point
            ++k;
        }
        i += 1;
    }
    if (j > mid)
    {
        k = 0;
    }
    else
    {
        k = mid - right;
    }
    for (j = i; j <= right; ++j)
    {
        // Move the rest of the elements to the end of the ready part
        v[j] = v2[j + k];
    }

}

void Datastructures::merge_sort(std::vector<Datastructures::Place*> &v,
                                unsigned long &left, unsigned long &right,
                                Coord &other_coord)
{
    if (left < right)
    {
        // Divide the vector in two and sort the parts
        unsigned long mid = (left + right) / 2;
        unsigned long new_left = mid + 1;
        merge_sort(v, left, mid, other_coord);
        merge_sort(v, new_left, right, other_coord);
        merge(v, left, mid, right, other_coord);
    }
}

void Datastructures::dijkstra(std::vector<std::tuple<Coord, WayID, Distance> > &vec,
                              Datastructures::Node *node, Coord &goal)
{
    // Reset all the nodes
    for (auto it = graph.begin(); it != graph.end(); ++it)
    {
        it->second->col = White;
        it->second->prev = {nullptr, NO_WAY};
        it->second->dist = -1;
    }

    // Change the first node's status as visited
    node->col = Gray;
    node->dist = 0;
    std::priority_queue<Node*> q = {};
    q.push(node);

    while (q.size() != 0)
    {
        // Take the first item in q and go through its adjacent nodes
        auto u = q.top();
        q.pop();
        for (auto v : u->adjs)
        {
            auto p = v.second.first;
            // If the goal node has been reached, add nodes to vec and return
            if (p->coord == goal)
            {
                p->prev = {&(*u), v.first};
                vec.push_back({p->coord, NO_WAY, u->dist+v.second.second});
                auto ptr = p->prev;
                while (ptr.first != nullptr)
                {
                    vec.push_back({ptr.first->coord, ptr.second, ptr.first->dist});
                    ptr = ptr.first->prev;
                }
                return;
            }
            // Visit an adjacent node if it hasn't been visited yet
            else if (p->col == White)
            {
                p->col = Gray;
                q.push(p);
            }
            relax(u, p, v.second.second, v.first);
        }

        // Set the handled node as finished and remove it from the list
        u->col = Black;
    }
}

void Datastructures::bfs(std::vector<std::tuple<Coord, WayID, Distance>> & vec,
                         Node* node, Coord& goal)
{
    // Reset all the nodes
    for (auto it = graph.begin(); it != graph.end(); ++it)
    {
        it->second->col = White;
        it->second->prev = {nullptr, NO_WAY};
        it->second->dist = 0;
    }

    // Change the first node's status as visited
    node->col = Gray;
    node->dist = 0;
    std::queue<Node*> q = {};
    q.push(node);

    while (q.size() != 0)
    {
        // Take the first item in q and go through its adjacent nodes
        auto u = q.front();
        q.pop();
        for (auto v : u->adjs)
        {
            auto p = v.second.first;
            // If the goal node has been reached, add nodes to vec and return
            if (p->coord == goal)
            {
                p->prev = {&(*u), v.first};
                vec.push_back({p->coord, NO_WAY, u->dist+v.second.second});
                auto ptr = p->prev;
                while (ptr.first != nullptr)
                {
                    vec.push_back({ptr.first->coord, ptr.second, ptr.first->dist});
                    ptr = ptr.first->prev;
                }
                return;
            }
            // Visit an adjacent node if it hasn't been visited yet
            else if (p->col == White)
            {
                p->col = Gray;
                p->dist = u->dist+v.second.second;
                p->prev = {&(*u), v.first};
                q.push(p);
            }          
        }

        // Set the handled node as finished
        u->col = Black;
    }
}

void Datastructures::relax(Datastructures::Node *u, Datastructures::Node *v,
                           Distance w, WayID u_id)
{
    if ((v->dist < 0) or (v->dist > (u->dist + w)))
    {
        v->dist = u->dist + w;
        v->prev = {u, u_id};
    }
}

void Datastructures::dfs(std::vector<std::tuple<Coord, WayID> > &vec, Datastructures::Node *node)
{
    // Reset all the nodes
    for (auto it = graph.begin(); it != graph.end(); ++it)
    {
        it->second->col = White;
        it->second->prev = {nullptr, NO_WAY};
    }

    //
    std::stack<Node*> q = {};
    q.push(node);
    while (q.size() != 0)
    {
        // extract the topmost node and continue to following nodes if
        // they haven't been dealt with yet.
        auto u = q.top();
        q.pop();
        if (u->col == White)
        {
            u->col = Gray;
            q.push(u);

            //
            for ( auto v : u->adjs )
            {
                auto p = v.second.first;
                if (p->col == White)
                {
                   if (p != u->prev.first)
                   {
                        p->prev = {u, v.first};
                        q.push(p);
                   }
                }
                // Return the path if a cycle was found
                else if (p->col == Gray && p != u->prev.first)
                {
                    vec.push_back({p->coord, NO_WAY});
                    std::pair<Node*, WayID> ptr = {u, v.first};
                    while (ptr.first != nullptr)
                    {
                        vec.push_back({ptr.first->coord, ptr.second});
                        ptr = ptr.first->prev;
                    }
                    return;

                }
            }
        }
        else
        {
            u->col = Black;
        }


    }
}

std::vector<WayID> Datastructures::all_ways()
{
    // Add all WayIDs to the vector vec
    std::vector<WayID> vec = {};
    for (auto it = ways.begin(); it != ways.end(); ++it)
    {
        vec.push_back(it->first);
    }
    return vec;
}

bool Datastructures::add_way(WayID id, std::vector<Coord> coords)
{
    Way new_way = {id, coords};
    std::shared_ptr<Way> wp( std::make_shared<Way>(new_way));   

    // If the way doesn't already exist, add it to the containers
    if (ways.insert({id, wp}).second == true)
    {
        auto it = coords.begin();
        auto it2 = coords.rbegin();
        auto a = graph.find(*it);
        auto b = graph.find(*it2);
        std::shared_ptr<Node> p1;
        std::shared_ptr<Node> p2;

        // Add a crossroads to the graph if it doesn't already exist
        if ( a != graph.end())
        {
            p1 = a->second;
        }
        else
        {
            Node new_node = {*it, {}, {}};
            std::shared_ptr<Node> np( std::make_shared<Node>(new_node));
            graph.insert({*it, np});
            p1 = np;
        }

        // Add a crossroads to the graph if it doesn't already exist
        if ( b != graph.end())
        {
            p2 = b->second;
        }
        else
        {
            Node new_node = {*it2, {}, {}};
            std::shared_ptr<Node> np( std::make_shared<Node>(new_node));
            graph.insert({*it2, np});
            p2 = np;
        }

        // Calculate way's length
        Distance dist = 0;
        for (unsigned long i = 1; i < coords.size(); ++i)
        {
            dist += (int) coord_distance(coords.at(i-1), coords.at(i));
        }

        // Add the way's ends to each other's neighbors and return true
        p1->adjs.insert({id, {&(*p2), dist}});
        p2->adjs.insert({id, {&(*p1), dist}});
        return true;
    }

    return false;
}

std::vector<std::pair<WayID, Coord>> Datastructures::ways_from(Coord xy)
{
    auto iter = graph.find(xy);
    std::vector<std::pair<WayID, Coord>> vec = {};

    // If the asked node exists, go through the ways departing from it and add
    // add the to the vector to be returned
    if (iter != graph.end())
    {
        for (auto way : iter->second->adjs)
        {
            vec.push_back({way.first, way.second.first->coord});
        }
    }
    return vec;
}

std::vector<Coord> Datastructures::get_way_coords(WayID id)
{
    // If the wanted way exists, return its coordinate vector
    auto a = ways.find(id);
    if ( a != ways.end())
    {
        return a->second->vec;

    }
    return {};
}

void Datastructures::clear_ways()
{
    graph.clear();
    ways.clear();
}

std::vector<std::tuple<Coord, WayID, Distance> > Datastructures::route_any(Coord fromxy, Coord toxy)
{
    std::vector<std::tuple<Coord, WayID, Distance>> vec = {};
    // Find the pointer to the starting node and find the shortest route using
    // breadth-first search
    auto start_node = graph.find(fromxy);
    if (start_node != graph.end())
    {
        bfs(vec, &(*start_node->second), toxy);
    }

    // If no way was found, return already
    if(vec.size() == 0)
    {
        return {{NO_COORD, NO_WAY, NO_DISTANCE}};
    }

    // Copy the elements of vec to v to sort them correctly and return v
    std::vector<std::tuple<Coord, WayID, Distance>> v = {};
    for ( auto it = vec.rbegin(); it < vec.rend(); ++it)
    {
        v.push_back(*it);
    }
    return v;
}

bool Datastructures::remove_way(WayID id)
{
    // Find the way to be erased from the ways
    auto it = ways.find(id);

    // If the way exists, remove it from ways and graph, and return true
    if (it != ways.end())
    {
        auto start_node = graph.find(*it->second->vec.begin());
        auto end_node = graph.find(*it->second->vec.rbegin());

        start_node->second->adjs.erase(id);
        end_node->second->adjs.erase(id);

        if(start_node->second->adjs.size() == 0)
        {
            graph.erase(start_node);
        }
        if (end_node->second->adjs.size() == 0)
        {
            graph.erase(end_node);
        }
        ways.erase(it);
        return true;
    }

    return false;
}

std::vector<std::tuple<Coord, WayID, Distance> > Datastructures::route_least_crossroads(Coord fromxy, Coord toxy)
{
    std::vector<std::tuple<Coord, WayID, Distance>> vec = {};
    // Find the pointer to the starting node and find the shortest route using
    // breadth-first search
    auto start_node = graph.find(fromxy);
    if (start_node != graph.end())
    {
        bfs(vec, &(*start_node->second), toxy);
    }

    // If no way was found, return already
    if(vec.size() == 0)
    {
        return {{NO_COORD, NO_WAY, NO_DISTANCE}};
    }

    // Copy the elements of vec to v to sort them correctly and return v
    std::vector<std::tuple<Coord, WayID, Distance>> v = {};
    for ( auto it = vec.rbegin(); it < vec.rend(); ++it)
    {
        v.push_back(*it);
    }
    return v;
}

std::vector<std::tuple<Coord, WayID> > Datastructures::route_with_cycle(Coord fromxy)
{
    std::vector<std::tuple<Coord, WayID> > vec = {};
    Node* start_node = &(*graph[fromxy]);
    if( start_node != nullptr)
    {
        dfs(vec, start_node);
    }

    // If no cycle was found, return information of that, else return vec
    if (vec.size() == 0)
    {
        return {{NO_COORD, NO_WAY}};
    }

    // Copy the elements of vec to v to sort them correctly and return v
    std::vector<std::tuple<Coord, WayID> > v = {};
    for ( auto it = vec.rbegin(); it < vec.rend(); ++it)
    {
        v.push_back(*it);
    }
    return v;
}

std::vector<std::tuple<Coord, WayID, Distance> > Datastructures::route_shortest_distance(Coord fromxy, Coord toxy)
{
    std::vector<std::tuple<Coord, WayID, Distance>> vec = {};
    // Find the pointer to the starting node and find the shortest route using
    // Dijkstra's algorithm
    auto start_node = graph.find(fromxy);
    if (start_node != graph.end())
    {
        dijkstra(vec, &(*start_node->second), toxy);
    }

    // If no way was found, return already
    if(vec.size() == 0)
    {
        return {{NO_COORD, NO_WAY, NO_DISTANCE}};
    }

    // Copy the elements of vec to v to sort them correctly and return v
    std::vector<std::tuple<Coord, WayID, Distance>> v = {};
    for ( auto it = vec.rbegin(); it < vec.rend(); ++it)
    {
        v.push_back(*it);
    }
    return v;
}

Distance Datastructures::trim_ways()
{
    // Replace this comment with your implementation
    return NO_DISTANCE;
}
